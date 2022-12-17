package com.zhexu.cs677_lab3.api.repository.impl;

import com.zhexu.cs677_lab3.api.bean.basic.Product;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForStock;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.UpdateConflictException;
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

    @Override
    public Stock addStock(Stock stock) {
        Stock stockInDb = getStockById(stock.getSellerId());
        if (null != stockInDb){
            return stockInDb;
        }
        try {
            add(stock);
        } catch (UpdateConflictException e){
            return getStockById(stock.getSellerId());
        }

        return getStockById(stock.getSellerId());
    }

    @Override
    public Stock getStockById(String stockId) {
        try {
            return get(stockId);
        } catch (DocumentNotFoundException e) {
            return null;
        }

    }

    @Override
    public Stock updateStock(Stock stock) {
        Stock stockInDb = getStockById(stock.getSellerId());
        if (null == stockInDb){
            return null;
        }
        update(stockInDb);

        return getStockById(stock.getSellerId());
    }

    @Override
    public Boolean consumeStock(String stockId, Product product, Integer number) {
        Stock stockInDb = getStockById(stockId);
        if (null == stockInDb){
            return Boolean.FALSE;
        }

        int stockUpdate = stockInDb.getStock().get(product) - number;

        if (stockUpdate < 0){
            return Boolean.FALSE;
        }

        stockInDb.getStock().put(product, stockUpdate);

        if (null == updateStock(stockInDb)){
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
