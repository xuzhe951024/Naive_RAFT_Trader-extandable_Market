package com.zhexu.cs677_lab3.business.rpcServer.service.trading;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.response.MarketTransactionResultResponse;

/**
 * @project: COMPSCI677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/16/22
 **/
public interface TradingDbService {
    MarketTransactionResultResponse tradDb(MarketTransaction transaction);
}
