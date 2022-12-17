package com.zhexu.cs677_lab3.business.raft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/4/22
 **/
public interface EventApplyInitiateService<T> {
    void setLogItem(RaftLogItem logItem);

    void broardCast() throws JsonProcessingException;
    Boolean collectedEnougthResponse();
    void commit() throws Exception;
    void rollback(RollbackMethod rollbackMethod) throws JsonProcessingException;
    void cleanMessageBroadCastMap ();
}
