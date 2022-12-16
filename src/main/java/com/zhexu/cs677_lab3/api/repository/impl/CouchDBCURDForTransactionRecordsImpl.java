package com.zhexu.cs677_lab3.api.repository.impl;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForTransactionRecords;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public class CouchDBCURDForTransactionRecordsImpl extends CouchDbRepositorySupport<MarketTransaction> implements CouchDBCURDForTransactionRecords {
    public CouchDBCURDForTransactionRecordsImpl(Class<MarketTransaction> type, CouchDbConnector db, boolean createIfNotExists) {
        super(type, db, createIfNotExists);
    }
    @Override
    public MarketTransaction addTransaction(MarketTransaction transaction) {
        MarketTransaction transactionInDb = getTransactionById(transaction.getTransactionId());
        if (null != transactionInDb){
            return transactionInDb;
        }

        add(transaction);
        return getTransactionById(transaction.getTransactionId());
    }

    @Override
    public MarketTransaction getTransactionById(String transactionId) {
       try {
           return get(transactionId);
       } catch (DocumentNotFoundException e){
           return null;
       }
    }
}
