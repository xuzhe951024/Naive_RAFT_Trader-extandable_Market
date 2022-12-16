package com.zhexu.cs677_lab3.api.bean;


import com.zhexu.cs677_lab3.api.bean.basic.Address;
import com.zhexu.cs677_lab3.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab3.api.bean.basic.Product;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.*;

import static com.zhexu.cs677_lab3.constants.Consts.*;
import static com.zhexu.cs677_lab3.constants.RoleConsts.*;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/25/22
 **/
@Log4j2
public class Role extends PeerBase implements Serializable {
    private Map<Integer, Product> productMap;

    private Map<String, Stock> stockMap;

    public Role(UUID id, Map<UUID, Address> neighbourPeerList, Address address, Map<Integer, Product> productMap, Map<String, Stock> stockMap) {
        super(id, neighbourPeerList, address);
        this.productMap = productMap;
        this.stockMap = stockMap;
    }

    public Map<String, Stock> getStockMap() {
        return stockMap;
    }

    public Integer getProductMapSize() {
        return this.productMap.size();
    }

    public Boolean isStockAvaliable(String sellerId, Integer productId){
        Product targetProduct = this.productMap.get(productId);
        return null != this.stockMap
                && null != this.stockMap.get(sellerId)
                && null != this.stockMap.get(sellerId).getStock()
                && null != this.stockMap.get(sellerId).getStock().get(targetProduct);
    }

    public Integer getStockByProductId(String sellerId, Integer productId) {
        Product targetProduct = this.productMap.get(productId);

        if (!isStockAvaliable(sellerId, productId)) {
            return -ONE;
        }

        return this.stockMap.get(sellerId).getStock().get(targetProduct);
    }

    public Integer consumeStockByProductId(String sellerId, Integer productId, Integer number) {
        Product targetProduct = this.productMap.get(productId);
        if (!isStockAvaliable(sellerId, productId)) {
            return -ONE;
        }

        Integer result = this.stockMap.get(sellerId).getStock().get(targetProduct) - number;

        if (result >= 0){
            this.stockMap.get(sellerId).getStock().put(targetProduct, result);
        }

        return result;
    }

    public void setStockMap(Map<String, Stock> stockMap) {
        this.stockMap = stockMap;
    }

    public Boolean isBuyer() {
        return MARKET_BUYER.equals(getPositionName());
    }

    public Boolean isSeller() {
        return MARKET_SELLER.equals(getPositionName()) ;
    }

    public void becomeBuyer() {
         setPositionName(MARKET_BUYER);
    }

    public void becomeSeller() {
        setPositionName(MARKET_SELLER);
    }

    public Boolean isStore() {
        return MARKET_STORE.equals(getPositionName());
    }

    public void becomeStore() {
        setPositionName(MARKET_STORE);
    }

    public Boolean isProxy() {
        return MARKET_PROXY.equals(getPositionName());
    }

    public void becomeProxy() {
        setPositionName(MARKET_PROXY);
    }

    public Map<Integer, Product> getProductMap() {
        return productMap;
    }

    public void setProductMap(Map<Integer, Product> productMap) {
        this.productMap = productMap;
    }

    @Override
    public String toString() {
        return "Role{" +
                "shopList=" + productMap +
                ", stock=" + stockMap +
                '}';
    }
}
