package com.zhexu.cs677_lab3.business.rpcServer.service.trading;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/12/22
 **/
public interface TradingRespService {
    Boolean checkIfProductAvailable(Integer productId, Integer stock);
    Integer consumeProduct(Integer productId, Integer number);
}
