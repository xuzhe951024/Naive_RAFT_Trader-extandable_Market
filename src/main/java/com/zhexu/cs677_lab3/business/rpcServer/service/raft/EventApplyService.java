package com.zhexu.cs677_lab3.business.rpcServer.service.raft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.bean.response.basic.BasicResponse;

import java.util.UUID;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/4/22
 **/
public interface EventApplyService {
//    BasicResponse
    public BasicResponse response(UUID logId);
    public BasicResponse applyNewMassage(RaftLogItem logItem) throws ClassNotFoundException, JsonProcessingException;
}
