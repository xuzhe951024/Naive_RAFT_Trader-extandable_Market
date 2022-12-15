package com.zhexu.cs677_lab3.api.repository.impl;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForStock;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public class CouchDBCURDForStockImpl extends CouchDbRepositorySupport<Stock> implements CouchDBCURDForStock {
    public CouchDBCURDForStockImpl(Class<Stock> type, CouchDbConnector db, boolean createIfNotExists) {
        super(type, db, createIfNotExists);
    }
    //TODO implement
}
