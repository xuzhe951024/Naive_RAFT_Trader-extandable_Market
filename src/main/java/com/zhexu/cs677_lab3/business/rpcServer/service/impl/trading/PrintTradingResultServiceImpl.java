package com.zhexu.cs677_lab3.business.rpcServer.service.impl.trading;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.bean.response.MarketTransactionResultResponse;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.PrintTradingResultService;
import lombok.extern.log4j.Log4j2;

import static com.zhexu.cs677_lab3.constants.Consts.ENTER;
import static com.zhexu.cs677_lab3.constants.Consts.IMPORTANT_LOG_WRAPPER;

/**
 * @project: COMPSCI677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
@Log4j2
public class PrintTradingResultServiceImpl implements PrintTradingResultService {

    @Override
    public void print(MarketTransactionResultResponse response) {

        MarketTransaction transactionBean = response.getTransaction();

        Long time = System.currentTimeMillis() - transactionBean.getLocalTimeStamp();
        SingletonFactory.setTransactionTime(SingletonFactory.getTransactionTime() + time);
        SingletonFactory.setTransactionNum(SingletonFactory.getTransactionNum() + 1);

       log.info(ENTER + IMPORTANT_LOG_WRAPPER);
       log.info(response.printBasic());
       log.info("Transaction: " +
               transactionBean.getTransactionId() +
               " takes " +
               time +
               " ms" +
               "\nAvarage transaction time: " +
               SingletonFactory.getTransactionTime() / SingletonFactory.getTransactionNum() +
               " ms\nTransaction details:" +
               transactionBean.toString());
       log.info(IMPORTANT_LOG_WRAPPER + ENTER);
    }
}
