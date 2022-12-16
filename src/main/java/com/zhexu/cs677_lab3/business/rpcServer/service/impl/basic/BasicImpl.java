package com.zhexu.cs677_lab3.business.rpcServer.service.impl.basic;

import com.zhexu.cs677_lab3.api.bean.response.basic.BasicResponse;
import com.zhexu.cs677_lab3.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.constants.ResponseCode;
import lombok.extern.log4j.Log4j2;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/11/22
 **/
@Log4j2
public class BasicImpl {
    protected PeerBase peer = SingletonFactory.getRole();
    protected BasicResponse checkIfLogSyncInProgress(){
        BasicResponse response = new BasicResponse();

        if(peer.isSyncLogInProgress()){
            response.setStatus(ResponseCode.STATUS_FORBIDDEN);
            response.setMessage("Log sync in progress");
            log.info("Log sync in progress, pause election voting.");
            return response;
        }

        if (peer.isNotARaftMember()){
            response.setMessage(ResponseCode.DESCRIPTION_FORBIDDEN);
            response.setMessage("Not a raft member!");
            log.info("Not a raft member, do not responsing to transaction intermidiate messages");
        }

        return response;
    }
}
