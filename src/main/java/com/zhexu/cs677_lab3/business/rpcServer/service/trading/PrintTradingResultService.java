package com.zhexu.cs677_lab3.business.rpcServer.service.trading;

import com.zhexu.cs677_lab3.api.bean.response.MarketTransactionResultResponse;

/**
 * @project: COMPSCI677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public interface PrintTradingResultService {
    void print(MarketTransactionResultResponse response);
}
