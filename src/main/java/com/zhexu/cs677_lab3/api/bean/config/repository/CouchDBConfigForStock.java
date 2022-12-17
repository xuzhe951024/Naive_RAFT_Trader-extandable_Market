package com.zhexu.cs677_lab3.api.bean.config.repository;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.raftLogMatenance.RaftLogItem;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.bean.config.repository.databaseInfo.CouchDBInfoForStock;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForStock;
import com.zhexu.cs677_lab3.api.repository.impl.CouchDBCURDForStockImpl;
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
public class CouchDBConfigForStock {
    @Autowired
    CouchDBInfoForStock couchDBInfoForStock;

    @Bean(name = "CouchDBCURDForStock")
    public CouchDBCURDForStock couchDbConnector() throws Exception {
        StdHttpClient.Builder builder = new StdHttpClient.Builder();
        builder.url(couchDBInfoForStock.getHost() + ":" + couchDBInfoForStock.getPort());
        builder.username(couchDBInfoForStock.getUsername());
        builder.connectionTimeout(TEN_THOUSAND);
        builder.socketTimeout(ONE_MILLION);
        builder.password(couchDBInfoForStock.getPassword());
        HttpClient httpClient = builder.build();
        CouchDbInstance couchDbInstance = new StdCouchDbInstance(httpClient);
        CouchDbConnector couchDbConnector = new StdCouchDbConnector(couchDBInfoForStock.getDatabase(),
                couchDbInstance);
        couchDbConnector.createDatabaseIfNotExists();

        CouchDBCURDForStock couchDBCURDForStock = new CouchDBCURDForStockImpl(Stock.class, couchDbConnector, true);
        log.info("CouchDb Connector for stock injection successful!");
        return couchDBCURDForStock;
    }
}
