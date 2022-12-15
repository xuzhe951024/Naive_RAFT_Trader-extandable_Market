package com.zhexu.cs677_lab3.api.bean.config.repository;

import com.zhexu.cs677_lab3.api.bean.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.bean.config.repository.databaseInfo.CouchDBInfoForTransactionRecords;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForTransactionRecords;
import com.zhexu.cs677_lab3.api.repository.impl.CouchDBCURDForTransactionRecordsImpl;
import lombok.extern.log4j.Log4j2;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.zhexu.cs677_lab3.constants.Consts.ONE_MILLION;
import static com.zhexu.cs677_lab3.constants.Consts.TEN_THOUSAND;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/15/22
 **/
@Configuration
@Log4j2
public class CouchDBConfigForTransactionRecords {
    @Autowired
    CouchDBInfoForTransactionRecords couchDBInfoForTransactionRecords;

    @Bean(name = "CouchDBCURDForTransactionRecords")
    public CouchDBCURDForTransactionRecords couchDbConnector() throws Exception {
        HttpClient httpClient = new StdHttpClient.Builder().url(couchDBInfoForTransactionRecords.getHost() + ":" + couchDBInfoForTransactionRecords.getPort())
                .username(couchDBInfoForTransactionRecords.getUsername()).connectionTimeout(TEN_THOUSAND).socketTimeout(ONE_MILLION)
                .password(couchDBInfoForTransactionRecords.getPassword()).build();
        CouchDbInstance couchDbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector couchDbConnector = new StdCouchDbConnector(couchDBInfoForTransactionRecords.getDatabase() +
                "_" +
                SingletonFactory.getRole().getSelfAddress().getDomain().replace(".", "_"),
                couchDbInstance);
        couchDbConnector.createDatabaseIfNotExists();

        CouchDBCURDForTransactionRecords couchDBCURDForTransactionRecords = new CouchDBCURDForTransactionRecordsImpl(MarketTransaction.class, couchDbConnector, true);
        log.info("CouchDb Connector for transaction records injection successful!");
        return couchDBCURDForTransactionRecords;
    }
}
