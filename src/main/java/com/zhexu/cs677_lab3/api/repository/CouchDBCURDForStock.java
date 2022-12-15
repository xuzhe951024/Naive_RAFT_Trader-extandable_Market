package com.zhexu.cs677_lab3.api.repository;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public interface CouchDBCURDForStock {
    //TODO implement
    Stock add(Stock stock);

    Stock get(String stockId);

    Stock update(Stock stock);
}
