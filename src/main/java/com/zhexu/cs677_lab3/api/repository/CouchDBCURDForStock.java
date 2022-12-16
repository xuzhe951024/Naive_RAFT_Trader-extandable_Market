package com.zhexu.cs677_lab3.api.repository;

import com.zhexu.cs677_lab3.api.bean.basic.Product;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public interface CouchDBCURDForStock {
    Stock addStock(Stock stock);

    Stock getStockById(String stockId);

    Stock updateStock(Stock stock);

    Boolean consumeStock(String stockId, Product product, Integer number);
}
