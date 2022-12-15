package com.zhexu.cs677_lab3.business.logEventHandler.Impl.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForRaftEvents;
import com.zhexu.cs677_lab3.business.logEventHandler.EventHandler;
import com.zhexu.cs677_lab3.utils.SpringContextUtils;

import java.util.UUID;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
public abstract class EventHandlerBase<T> implements EventHandler {
    protected RaftLogItem saveEventBeanToCouchDb(T eventBean, UUID logId, UUID eventId) throws Exception {
        CouchDBCURDForRaftEvents couchDbCURDForRaftEvents = SpringContextUtils.getBean(CouchDBCURDForRaftEvents.class);
        ObjectMapper objectMapper = new ObjectMapper();
        RaftLogItem raftLogItem = new RaftLogItem();
        raftLogItem.setLogId(logId);
        raftLogItem.setEventId(eventId);
        raftLogItem.setEventClassName(eventBean.getClass().getName());
        raftLogItem.setEventJSONString(objectMapper.writeValueAsString(eventBean));
        raftLogItem.setTermAndIndex((RaftTransBase) eventBean);
        return couchDbCURDForRaftEvents.addRaftLog(raftLogItem);
    }
}
