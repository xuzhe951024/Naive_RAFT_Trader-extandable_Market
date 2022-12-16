package com.zhexu.cs677_lab3.business.rpcServer.service.trading;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.response.basic.BasicResponse;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/12/22
 **/
public interface TradingLaunchService {
    BasicResponse launchTradingTransaction(MarketTransaction transaction);
}
