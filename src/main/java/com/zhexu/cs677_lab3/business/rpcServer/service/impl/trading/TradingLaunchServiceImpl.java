package com.zhexu.cs677_lab3.business.rpcServer.service.impl.trading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.Role;
import com.zhexu.cs677_lab3.api.bean.basic.Address;
import com.zhexu.cs677_lab3.api.bean.response.MarketTransactionResultResponse;
import com.zhexu.cs677_lab3.api.bean.response.basic.BasicResponse;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.bean.config.TimerConfig;
import com.zhexu.cs677_lab3.business.raft.EventApplyInitiateService;
import com.zhexu.cs677_lab3.business.raft.Impl.EventApplyInitiateServiceImpl;
import com.zhexu.cs677_lab3.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab3.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.PrintTradingResultService;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.TradingDbService;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.TradingLaunchService;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.TradingRespService;
import com.zhexu.cs677_lab3.constants.ResponseCode;
import com.zhexu.cs677_lab3.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;

import static com.zhexu.cs677_lab3.constants.Consts.*;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/12/22
 **/
@Log4j2
public class TradingLaunchServiceImpl implements TradingLaunchService {
    Role role = SingletonFactory.getRole();

    /**
     * @param transaction
     * @return
     */
    @Override
    public BasicResponse launchTradingTransaction(MarketTransaction transaction) {
        BasicResponse response = new BasicResponse();

        log.info(MARKET_LOG_PREFIX + "Market transaction request reveived:" + transaction.toString());

        if (role.isNotARaftMember()) {
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("Transaction could only handle by trader:\npeer:" +
                    role.getSelfAddress().getDomain() +
                    "is not a trader!");
            log.error(MARKET_LOG_PREFIX + "Self is not a trader, can not handle transaction requests");
            return response;
        }

        ThreadPoolTaskExecutor threadPoolTaskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
        threadPoolTaskExecutor.submit(new Thread(() -> {
            try {
                startTrading(transaction);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        return response;
    }

    private void startTrading(MarketTransaction transaction) throws Exception {
        MarketTransactionResultResponse resultResponse = new MarketTransactionResultResponse();

        log.info(MARKET_LOG_PREFIX + "Starting find seller");

        /*
         *
         *    1. Cache with strict consistency
         *      a. find seller in cache
         *      b. consensus
         *      c. update cache
         *          c1. response purchas result to buyer
         *      d. write to db
         *          d1. response shipment to buyer
         *    2. no cache
         *       a. use db service, and response to buyer
         *
         */

        if (role.isNotARaftMember()) {
            log.error(MARKET_LOG_PREFIX +role.getSelfAddress().getDomain() + " is not a trader!");
            return;
        }

        if (SingletonFactory.runWithoutCache()) {
            tradeToDb(transaction);
        }

        List<String> stockAvailableList = new LinkedList<>() {{
            if (null != transaction.getSeller()) {
                log.debug(MARKET_LOG_PREFIX +"Seller " + transaction.getSeller() + " restocking");
                add(transaction.getSeller().toString());

            } else {
                role.getStockMap().forEach((k, v) -> {
                    TradingRespService tradingRespService = new TradingRespServiceImpl();
                    if (tradingRespService.checkIfProductAvailable(k,
                            transaction.getProduct().getProductId(),
                            transaction.getNumber())) {
                        add(k);
                    }
                });
            }
        }};


        if (stockAvailableList.isEmpty()) {
            log.info(MARKET_LOG_PREFIX +
                    "No product: " +
                    transaction.getProduct().getProductName() +
                    " stock: " +
                    transaction.getStock() +
                    " available!");
            transaction.setSuccessful(Boolean.FALSE);
            transaction.setRemark("Stock insufficient!");
            resultResponse.setMessage(TRADER_FAIL_MESSAGE);
            resultResponse.setTransaction(transaction);
            resultResponse.setStatus(ResponseCode.STATUS_FORBIDDEN);
        }

        log.info(MARKET_LOG_PREFIX + "Seller List: " + stockAvailableList);

        ObjectMapper objectMapper = new ObjectMapper();

        for (String sellerId : stockAvailableList) {
            log.debug(MARKET_LOG_PREFIX +"Start to trading process:");
            TradingRespService tradingRespService = new TradingRespServiceImpl();
            Integer stock = tradingRespService.consumeProduct(
                    sellerId,
                    transaction.getProduct().getProductId(),
                    transaction.getNumber());
            if (stock.intValue() < 0) {
                log.debug(MARKET_LOG_PREFIX +"checking before lock stock: insufficient stock!");
                continue;
            }

            log.debug(MARKET_LOG_PREFIX +"checking before lock stock passed, now assemble transaction bean to launch trading");
            role.getRaftBase().increaseIndex();
            transaction.setTermAndIndex(role.getRaftBase());
            transaction.setSeller(UUID.fromString(sellerId));
            transaction.setStock(stock);

            EventApplyInitiateService eventApplyInitiateService = new EventApplyInitiateServiceImpl();

            RaftLogItem logItem = new RaftLogItem();

            logItem.setEventClassName(transaction.getClass().getName());
            logItem.setEventJSONString(objectMapper.writeValueAsString(transaction));
            logItem.setTermAndIndex(transaction);
            logItem.setEventId(transaction.getEventId());
            log.debug(MARKET_LOG_PREFIX +"transaction:\n " +
                    logItem +
                    "\nis ready to be apply!");

            eventApplyInitiateService.setLogItem(logItem);
            eventApplyInitiateService.broardCast();

            log.info(MARKET_LOG_PREFIX +"Now sleep for " +
                    TimerConfig.getLogBroadCastApplyWaitTime() +
                    "ms to wait for responses.");

            Thread.sleep(TimerConfig.getLogBroadCastApplyWaitTime());

            if (!eventApplyInitiateService.collectedEnougthResponse()) {

                log.info(MARKET_LOG_PREFIX +
                        "Did not collect enough broadcast responses for transaction:" +
                        transaction.getTransactionId() +
                        "rolling back now.");
//
//                MarketTransaction rollbackTransaction = new MarketTransaction();
//
//                role.getRaftBase().increaseIndex();
//
//                rollbackTransaction.setRollBackTransaction(transaction);
//                rollbackTransaction.setTermAndIndex(role.getRaftBase());
//
//                Integer rollBackStock = tradingRespService.consumeProduct(
//                        transaction.getSeller().toString(),
//                        transaction.getProduct().getProductId(),
//                        -transaction.getNumber());
//                rollbackTransaction.setStock(rollBackStock);
//                RaftLogItem rollBackLogItem = new RaftLogItem();
//
//                rollBackLogItem.setEventClassName(rollbackTransaction.getClass().getName());
//                rollBackLogItem.setEventJSONString(objectMapper.writeValueAsString(rollbackTransaction));
//                rollBackLogItem.setTermAndIndex(rollbackTransaction);
//                rollBackLogItem.setEventId(transaction.getEventId());
//
//                eventApplyInitiateService.rollback(() -> rollbackeMarketTransaction(rollBackLogItem));
            }

            log.info(MARKET_LOG_PREFIX +"Now committing transaction: " + transaction);
            eventApplyInitiateService.commit();
            resultResponse.setMessage(TRADER_SUCCESS_MESSAGE);
            resultResponse.setTransaction(transaction);

            tradeToDb(transaction);
            log.info(MARKET_LOG_PREFIX +
                    "Transaction: " +
                    transaction +
                    "\nhas been committed");

            break;

        }

        RPCInvocationHandler printResultHandler = new RPCInvocationHandler(transaction.getBuyerAdd());
        PrintTradingResultService printTradingResultService = ProxyFactory.getInstance(PrintTradingResultService.class, printResultHandler);
        printTradingResultService.print(resultResponse);

    }

    private void tradeToDb(MarketTransaction transaction) {
        log.info(MARKET_LOG_PREFIX +"Now saving to db:");
        TradingDbService tradingDbService = new TradingDbServiceImpl();
        MarketTransactionResultResponse resultResponse = tradingDbService.tradDb(transaction);

        RPCInvocationHandler handler = new RPCInvocationHandler(transaction.getBuyerAdd());
        PrintTradingResultService printTradingResultService = ProxyFactory.getInstance(PrintTradingResultService.class, handler);
        printTradingResultService.print(resultResponse);
    }

    private void rollbackeMarketTransaction(RaftLogItem rollBackLogItem) throws JsonProcessingException {
        EventApplyInitiateService rollBackApply = new EventApplyInitiateServiceImpl();

        rollBackApply.setLogItem(rollBackLogItem);
        rollBackApply.broardCast();
        rollBackApply.cleanMessageBroadCastMap();
    }


}
