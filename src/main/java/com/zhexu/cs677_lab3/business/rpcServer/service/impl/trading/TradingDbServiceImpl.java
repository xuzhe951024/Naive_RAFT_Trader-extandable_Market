package com.zhexu.cs677_lab3.business.rpcServer.service.impl.trading;

import com.zhexu.cs677_lab3.api.bean.Role;
import com.zhexu.cs677_lab3.api.bean.basic.Product;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.bean.response.MarketTransactionResultResponse;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForStock;
import com.zhexu.cs677_lab3.api.repository.impl.CouchDBCURDForTransactionRecordsImpl;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.TradingDbService;
import com.zhexu.cs677_lab3.constants.ResponseCode;
import com.zhexu.cs677_lab3.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import static com.zhexu.cs677_lab3.constants.Consts.STORE_FAIL_MESSAGE;
import static com.zhexu.cs677_lab3.constants.Consts.STORE_SUCCESS_MESSAGE;

/**
 * @project: COMPSCI677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/16/22
 **/
@Log4j2
public class TradingDbServiceImpl implements TradingDbService {
    Role role = SingletonFactory.getRole();
    @Override
    public MarketTransactionResultResponse tradDb(MarketTransaction transaction) {
        MarketTransactionResultResponse resultResponse = new MarketTransactionResultResponse();
        CouchDBCURDForStock dbCurdForStock = SpringContextUtils.getBean(CouchDBCURDForStock.class);
        CouchDBCURDForTransactionRecordsImpl dbCurdForTransactionRecords = SpringContextUtils.getBean(CouchDBCURDForTransactionRecordsImpl.class);

        if(transaction.getNumber() < 0){

            log.debug("[DBtransaction:]Transaction seller is not null, buyer is buying!");
            for(String k: role.getStockMap().keySet()){
                log.debug("[DBtransaction:] trying to find stock for seller: " + k);
                Stock dbStockItem = dbCurdForStock.getStockById(k);
                log.debug("[DBtransaction:] find dbStockItem: " + dbStockItem.toString());
                Map<Product, Integer> stockMap = dbStockItem.getStock();
                Integer result = stockMap.get(transaction.getProduct()) - transaction.getNumber();
                if(result < 0){
                    log.debug("[DBtransaction:]consume result < 0, " + result);
                    continue;
                }
                log.debug("[DBtransaction:]consume result is right, " + result);
                return getMarketTransactionResultResponse(transaction, resultResponse, dbCurdForStock, dbCurdForTransactionRecords, dbStockItem, stockMap, result);
            }

            log.debug("[DBtransaction:]Insufficient stock!");
            resultResponse.setStatus(ResponseCode.STATUS_FORBIDDEN);
            resultResponse.setMessage(STORE_FAIL_MESSAGE);
            transaction.setSuccessful(Boolean.FALSE);
            transaction.setRemark("Stock insufficient!");
            resultResponse.setTransaction(transaction);
            dbCurdForTransactionRecords.add(transaction);
            return resultResponse;

        } else {
            log.debug("[DBtransaction:]Transaction seller is null, seller is restocking!");
            Stock dbStockItem = dbCurdForStock.getStockById(transaction.getSeller().toString());
            Map<Product, Integer> stockMap = dbStockItem.getStock();
            Integer result = stockMap.get(transaction.getProduct()) - transaction.getNumber();
            log.debug("[DBtransaction:]seller reStock of stock: " + result);
            return getMarketTransactionResultResponse(transaction, resultResponse, dbCurdForStock, dbCurdForTransactionRecords, dbStockItem, stockMap, result);
        }
    }

    private MarketTransactionResultResponse getMarketTransactionResultResponse(MarketTransaction transaction, MarketTransactionResultResponse resultResponse, CouchDBCURDForStock dbCurdForStock, CouchDBCURDForTransactionRecordsImpl dbCurdForTransactionRecords, Stock dbStockItem, Map<Product, Integer> stockMap, Integer result) {
        stockMap.put(transaction.getProduct(), result);
        dbStockItem.setStock(stockMap);
        dbCurdForStock.updateStock(dbStockItem);
        resultResponse.setMessage(STORE_SUCCESS_MESSAGE);
        transaction.setSuccessful(Boolean.TRUE);
        resultResponse.setTransaction(transaction);
        dbCurdForTransactionRecords.add(transaction);
        return resultResponse;
    }
}
