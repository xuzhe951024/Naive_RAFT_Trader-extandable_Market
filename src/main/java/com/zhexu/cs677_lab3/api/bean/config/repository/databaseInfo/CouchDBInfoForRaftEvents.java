package com.zhexu.cs677_lab3.api.bean.config.repository.databaseInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 11/28/22
 **/

@Component
public class CouchDBInfoForRaftEvents {

    @Value(value = "${couchdb_raft_events.host}")
    private String host;

    @Value(value = "${couchdb_raft_events.port}")
    private String port;

    @Value(value = "${couchdb_raft_events.database}")
    private String database;

    @Value(value = "${couchdb_raft_events.username}")
    private String username;

    @Value(value = "${couchdb_raft_events.password}")
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
