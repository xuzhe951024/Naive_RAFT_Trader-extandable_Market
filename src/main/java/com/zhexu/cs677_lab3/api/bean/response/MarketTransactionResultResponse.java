package com.zhexu.cs677_lab3.api.bean.response;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.response.basic.BasicResponse;

/**
 * @project: COMPSCI677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
public class MarketTransactionResultResponse extends BasicResponse {
    public MarketTransactionResultResponse(){
        super();
    }
    private MarketTransaction transaction;

    public MarketTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(MarketTransaction transaction) {
        this.transaction = transaction;
    }

    public String printBasic(){
        return super.toString();
    }

    @Override
    public String toString() {
        return "MarketTransactionResultResponse{" +
                super.toString() +
                "transaction=" + transaction +
                '}';
    }
}
