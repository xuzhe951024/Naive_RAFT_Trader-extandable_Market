package com.zhexu.cs677_lab3.business.rpcServer.service.impl.trading;

import com.zhexu.cs677_lab3.api.bean.Role;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.TradingRespService;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/12/22
 **/
public class TradingRespServiceImpl implements TradingRespService {
    Role role = SingletonFactory.getRole();

    /**
     * check if product and stock available
     * @param productId
     * @param stock
     * @return
     */
    @Override
    public Boolean checkIfProductAvailable(String sellerId, Integer productId, Integer stock) {
        if (role.isNotARaftMember()){
            return Boolean.FALSE;
        }
        return role.getStockByProductId(sellerId, productId) - stock >= 0;
    }

    /**
     * @param productId
     * @param number
     * @return
     */
    @Override
    public Integer consumeProduct(String sellerId, Integer productId, Integer number) {
        if(role.isNotARaftMember()){
            return -1;
        }
        return role.consumeStockByProductId(sellerId, productId, number);
    }
}
