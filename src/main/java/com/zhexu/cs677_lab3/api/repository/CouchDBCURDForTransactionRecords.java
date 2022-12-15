package com.zhexu.cs677_lab3.api.repository;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public interface CouchDBCURDForTransactionRecords {
    //TODO implement
    MarketTransaction add(MarketTransaction transaction);

    MarketTransaction get(String transactionId);
}
