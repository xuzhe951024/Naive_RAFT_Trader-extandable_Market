package com.zhexu.cs677_lab3.api.bean.config.repository;

import com.zhexu.cs677_lab3.api.bean.config.repository.databaseInfo.CouchDBInfoForStock;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
@Configuration
@Log4j2
public class CouchDBConfigForStock {
    @Autowired
    CouchDBInfoForStock couchDBInfoForStock;

//    @Bean(name = "CouchDBCURDForStock")
//    public CouchDBCURDForStock couchDbConnector() throws Exception {
//        HttpClient httpClient = new StdHttpClient.Builder().url(couchDBInfoForStock.getHost() + ":" + couchDBInfoForStock.getPort())
//                .username(couchDBInfoForStock.getUsername()).connectionTimeout(TEN_THOUSAND).socketTimeout(ONE_MILLION)
//                .password(couchDBInfoForStock.getPassword()).build();
//        CouchDbInstance couchDbInstance = new StdCouchDbInstance(httpClient);
//        CouchDbConnector couchDbConnector = new StdCouchDbConnector(couchDBInfoForStock.getDatabase() +
//                "_" +
//                SingletonFactory.getRole().getSelfAddress().getDomain().replace(".", "_"),
//                couchDbInstance);
//        couchDbConnector.createDatabaseIfNotExists();
//
//        //TODO implement bean
//        CouchDBCURDForStock couchDBCURDForStock = new CouchDBCURDForStockImpl(RaftLogItem.class, couchDbConnector, true);
//        log.info("CouchDb Connector for stock injection successful!");
//        return couchDBCURDForStock;
}
