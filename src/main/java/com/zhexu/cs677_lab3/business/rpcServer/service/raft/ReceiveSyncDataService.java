package com.zhexu.cs677_lab3.business.rpcServer.service.raft;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.SerializableRaftLogListWrapper;
import com.zhexu.cs677_lab3.api.bean.response.basic.BasicResponse;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/9/22
 **/
public interface ReceiveSyncDataService {
    BasicResponse receiveData(SerializableRaftLogListWrapper raftLogItemList);
}
