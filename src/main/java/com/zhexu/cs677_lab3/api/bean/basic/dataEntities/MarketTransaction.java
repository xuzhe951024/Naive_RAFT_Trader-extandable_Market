package com.zhexu.cs677_lab3.api.bean.basic.dataEntities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhexu.cs677_lab3.api.bean.basic.Address;
import com.zhexu.cs677_lab3.api.bean.basic.Product;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.UUID;

import static com.zhexu.cs677_lab3.constants.Consts.TRANSACTION_ROLLING_BACK_PREFIX;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/10/22
 **/
public class MarketTransaction extends RaftTransBase implements Serializable {
    @JsonProperty(value = "_rev")
    private String revision;
    @JsonProperty(value = "_id")
    private String transactionId = UUID.randomUUID().toString();
    private UUID buyer;
    private UUID seller;
    private Product product;
    private Integer number;
    private Boolean successful = Boolean.TRUE;
    private String remark;
    private Integer stock;
    private UUID eventId = UUID.randomUUID();
    private final Long localTimeStamp = System.currentTimeMillis();

    private Address buyerAdd;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getBuyer() {
        return buyer;
    }

    public void setBuyer(UUID buyer) {
        this.buyer = buyer;
    }

    public UUID getSeller() {
        return seller;
    }

    public void setSeller(UUID seller) {
        this.seller = seller;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if(StringUtils.isEmpty(this.remark)){
            this.remark = product.getProductName();
        }
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Boolean isMainSeller(UUID id){
        return null != this.seller && this.seller.equals(id);
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setRollBackTransaction(MarketTransaction rollBackTransaction){
        this.eventId = rollBackTransaction.getEventId();
        this.product = rollBackTransaction.getProduct();
        this.seller = rollBackTransaction.getSeller();
        this.buyer = rollBackTransaction.getBuyer();
        this.remark = TRANSACTION_ROLLING_BACK_PREFIX + rollBackTransaction.getTransactionId();
        this.number = -rollBackTransaction.getNumber();
        this.successful = Boolean.FALSE;
        this.buyerAdd = rollBackTransaction.getBuyerAdd();
    }

    public Long getLocalTimeStamp() {
        return localTimeStamp;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Address getBuyerAdd() {
        return buyerAdd;
    }

    public void setBuyerAdd(Address buyerAdd) {
        this.buyerAdd = buyerAdd;
    }

    @Override
    public String toString() {
        return "MarketTransaction{" +
                "revision='" + revision + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", buyer=" + buyer +
                ", seller=" + seller +
                ", product=" + product +
                ", number=" + number +
                ", successful=" + successful +
                ", remark='" + remark + '\'' +
                ", stock=" + stock +
                ", eventId=" + eventId +
                ", localTimeStamp=" + localTimeStamp +
                ", buyerAdd=" + buyerAdd +
                '}';
    }
}
