package com.zhexu.cs677_lab3.api.bean.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.zhexu.cs677_lab3.api.bean.basic.Product;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import com.zhexu.cs677_lab3.api.bean.config.basic.InitConfigBasic;
import com.zhexu.cs677_lab3.utils.ProductDeSerializer;

import java.util.Map;

import static com.zhexu.cs677_lab3.constants.RoleConsts.MARKET_BUYER;
import static com.zhexu.cs677_lab3.constants.RoleConsts.MARKET_SELLER;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class InitConfigForRole extends InitConfigBasic {
    private String positionName;
    @JsonProperty("stock")
    private Map<String, Stock> stock;

    private Integer maxStock;


    public Map<String, Stock> getStock() {
        return stock;
    }

    public void setStock(Map<String, Stock> stock) {
        this.stock = stock;
    }

    public Integer getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
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
}
