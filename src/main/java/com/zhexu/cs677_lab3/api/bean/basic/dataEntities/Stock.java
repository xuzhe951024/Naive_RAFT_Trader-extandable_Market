package com.zhexu.cs677_lab3.api.bean.basic.dataEntities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhexu.cs677_lab3.api.bean.basic.Product;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * @project: CS677_LAB2
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public class Stock implements Serializable {
    @JsonProperty(value = "_rev")
    private String revision;
    @JsonProperty(value = "_id")
    private UUID sellerId;
    private Map<Product, Integer> stock;

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    public Map<Product, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<Product, Integer> stock) {
        this.stock = stock;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "sellerId=" + sellerId +
                ", stock=" + stock +
                '}';
    }
}
