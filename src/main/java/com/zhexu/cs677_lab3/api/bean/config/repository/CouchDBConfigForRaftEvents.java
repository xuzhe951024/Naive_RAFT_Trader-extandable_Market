package com.zhexu.cs677_lab3.api.bean.config.repository;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.bean.config.repository.databaseInfo.CouchDBInfoForRaftEvents;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForRaftEvents;
import com.zhexu.cs677_lab3.api.repository.impl.CouchDBCURDForRaftEventsImpl;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.log4j.Log4j2;

import static com.zhexu.cs677_lab3.constants.Consts.ONE_MILLION;
import static com.zhexu.cs677_lab3.constants.Consts.TEN_THOUSAND;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/
@Configuration
@Log4j2
public class CouchDBConfigForRaftEvents {

    @Autowired
    CouchDBInfoForRaftEvents couchDBInfoForRaftEvents;

    @Bean(name = "CouchDbCURDForRaftEvents")
    public CouchDBCURDForRaftEvents couchDbConnector() throws Exception {
        StdHttpClient.Builder builder = new StdHttpClient.Builder();
        builder.url(couchDBInfoForRaftEvents.getHost() + ":" + couchDBInfoForRaftEvents.getPort());
        builder.username(couchDBInfoForRaftEvents.getUsername());
        builder.connectionTimeout(TEN_THOUSAND);
        builder.socketTimeout(ONE_MILLION);
        builder.password(couchDBInfoForRaftEvents.getPassword());
        HttpClient httpClient = builder.build();
        CouchDbInstance couchDbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector couchDbConnector = new StdCouchDbConnector(couchDBInfoForRaftEvents.getDatabase() +
                "_" +
                SingletonFactory.getRole().getSelfAddress().getDomain().replace(".", "_"),
                couchDbInstance);
        couchDbConnector.createDatabaseIfNotExists();

        CouchDBCURDForRaftEvents couchDbCURDForRaftEvents = new CouchDBCURDForRaftEventsImpl(RaftLogItem.class, couchDbConnector, true);
        log.info("CouchDb Connector injection successful!");
        return couchDbCURDForRaftEvents;
    }

}
