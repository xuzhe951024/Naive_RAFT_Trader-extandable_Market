package com.zhexu.cs677_lab3.api.repository.impl;


import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.RaftTransBase;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForRaftEvents;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
public class CouchDBCURDForRaftEventsImpl extends CouchDbRepositorySupport<RaftLogItem> implements CouchDBCURDForRaftEvents {
    public CouchDBCURDForRaftEventsImpl(Class<RaftLogItem> type, CouchDbConnector db, boolean createIfNotExists) {
        super(type, db, createIfNotExists);
    }


    @Override
    public RaftLogItem addRaftLog(RaftLogItem logItem) throws Exception {
//        connector.create(logItem.getLogId().toString(), logItem);
//        return connector.get(RaftLogItem.class, logItem.getLogId().toString());
        logItem.setDocumentId();
        RaftLogItem logItemInDb = querayByTermAndIndex(logItem);
        if (null != logItemInDb &&
                logItem.getJsonStringHashCode().equals
                        (logItemInDb.getJsonStringHashCode())){

            return get(logItem.getId());

        }

        add(logItem);

        return get(logItem.getId());
    }



    /**
     * @param raftTransBase
     * @return
     */
    @Override
    public RaftLogItem querayByTermAndIndex(RaftTransBase raftTransBase) {
        try {
            RaftLogItem result = get(raftTransBase.generateDocumentId());
            return result;
        } catch (DocumentNotFoundException documentNotFoundException){
            return null;
        }
    }


}
