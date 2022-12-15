package com.zhexu.cs677_lab3.api.repository.impl;

import com.zhexu.cs677_lab3.api.bean.MarketTransaction;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForTransactionRecords;
import org.ektorp.CouchDbConnector;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public class CouchDBCURDForTransactionRecordsImpl implements CouchDBCURDForTransactionRecords {
    public CouchDBCURDForTransactionRecordsImpl(Class<MarketTransaction> marketTransactionClass, CouchDbConnector couchDbConnector, boolean createIfNotExists) {

    }
    //TODO implement
}
