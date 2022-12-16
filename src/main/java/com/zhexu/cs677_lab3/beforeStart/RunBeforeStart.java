package com.zhexu.cs677_lab3.beforeStart;

import com.zhexu.cs677_lab3.api.bean.Role;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForStock;
import com.zhexu.cs677_lab3.business.rpcServer.RpcServer;
import com.zhexu.cs677_lab3.utils.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


import static com.zhexu.cs677_lab3.constants.Consts.*;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/7/22
 **/
@Component
@Log4j2
@Order(value = 1)
public class RunBeforeStart implements CommandLineRunner {

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) {
        SingletonFactory.setRpcBufferSize(Integer.parseInt(args[TWO]));
        SingletonFactory.setRunMode(Integer.parseInt(args[FOUR]));
        Role role = SingletonFactory.getRole();

        CouchDBCURDForStock couchDBCURDForStock = SpringContextUtils.getBean(CouchDBCURDForStock.class);

        role.getStockMap().forEach((k, v) -> {
            couchDBCURDForStock.addStock(v);
        });

        RpcServer rpcServer = new RpcServer();
        log.debug("Thread pool size: "+
                threadPoolTaskExecutor.getPoolSize() +
                " Thread pool maxSize: " +
                threadPoolTaskExecutor.getMaxPoolSize() +
                " Thread queue size: "+
                threadPoolTaskExecutor.getQueueSize());
        threadPoolTaskExecutor.submit(new Thread(() -> {
            rpcServer.openServer(role.getSelfAddress().getPort());
        }));
    }
}
