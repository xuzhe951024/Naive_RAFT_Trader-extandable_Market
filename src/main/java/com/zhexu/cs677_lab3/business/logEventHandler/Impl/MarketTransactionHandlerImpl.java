package com.zhexu.cs677_lab3.business.logEventHandler.Impl;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.Role;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.business.logEventHandler.Impl.abstracts.EventHandlerBase;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/10/22
 **/
@Log4j2
public class MarketTransactionHandlerImpl extends EventHandlerBase {
    private MarketTransaction transactionBean;
    private UUID logId;
    private UUID eventId;
    private Role role = SingletonFactory.getRole();

    /**
     * @param eventBean
     * @param logId
     * @param eventId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean run(Object eventBean, UUID logId, UUID eventId) throws Exception {
        this.transactionBean = (MarketTransaction) eventBean;
        this.logId = logId;
        this.eventId = eventId;

        if (null == this.transactionBean) {
            log.error("Event bean can not be null!");
            return Boolean.FALSE;
        }

        if (!role.isFollower()) {
            log.info("Peer: " +
                    role.getSelfAddress().getDomain() +
                    "dose not legally apply the transaction!");
            return Boolean.FALSE;
        }

        log.info("Save:\n" +
                saveEventBeanToCouchDb(this.transactionBean, this.logId, this.eventId) +
                "\nto couchDb");

        log.info("Now update local stock cache.");
        role.consumeStockByProductId(transactionBean.getSeller().toString(),
                transactionBean.getProduct().getProductId(),
                transactionBean.getNumber());

        return Boolean.TRUE;
    }

    public MarketTransaction getTransactionBean() {
        return transactionBean;
    }

    public void setTransactionBean(MarketTransaction transactionBean) {
        this.transactionBean = transactionBean;
    }

    public UUID getLogId() {
        return logId;
    }

    public void setLogId(UUID logId) {
        this.logId = logId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

}
