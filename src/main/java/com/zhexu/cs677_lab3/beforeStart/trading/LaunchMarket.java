package com.zhexu.cs677_lab3.beforeStart.trading;

import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.Role;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.business.rpcClient.proxy.ProxyFactory;
import com.zhexu.cs677_lab3.business.rpcClient.proxy.RPCInvocationHandler;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.TradingLaunchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.zhexu.cs677_lab3.constants.Consts.*;

/**
 * @project: CS677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/13/22
 **/
@Component
@Log4j2
@Order(value = 3)
public class LaunchMarket implements CommandLineRunner {

    Role role = SingletonFactory.getRole();

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        Long sleepBeforeStart = Long.parseLong(args[ZERO]);
        SingletonFactory.setTestNum(Integer.parseInt(args[ONE]));
        Integer maxStock = Integer.parseInt(args[THREE]);

        SingletonFactory.setMaxStock(maxStock);

//        Thread.sleep(sleepBeforeStart);
//
//        for (int i = 0; i < numberOfTests; i++) {
//
//            Thread.sleep(10);
//
//        }

        int counter = 0;

        Timer tradingTimer = new Timer();
        TimerTask sendHeartPulseTask = new TimerTask() {
            @Override
            public void run() {

                if (SingletonFactory.getTransactionNum() >= SingletonFactory.getTestNum()) {
                    log.info(IMPORTANT_LOG_WRAPPER);
                    log.info("finished !");
                    log.info(IMPORTANT_LOG_WRAPPER);
                    cancel();
                }
                startBusiness();


            }
        };

        tradingTimer.schedule(sendHeartPulseTask, sleepBeforeStart, ONE_THOUSAND);
    }

    private Boolean startBusiness() {
        if (role.isARaftMember()
                || null == role.getNeighbourPeerMap()
                || role.getNeighbourPeerMap().isEmpty()) {
            return Boolean.FALSE;
        }

        role.getNeighbourPeerMap().forEach((k, v) -> {
            RPCInvocationHandler handler = new RPCInvocationHandler(v);
            TradingLaunchService tradingLaunchService = ProxyFactory.getInstance(TradingLaunchService.class, handler);

            Random random = new Random();
            MarketTransaction transaction = new MarketTransaction();

            if (role.isBuyer()) {
                transaction.setBuyer(role.getId());
                transaction.setNumber(random.nextInt(SingletonFactory.getMaxStock()));
            }

            if (role.isSeller()) {
                transaction.setBuyer(role.getId());
                transaction.setSeller(role.getId());
                transaction.setNumber(-SingletonFactory.getMaxStock());
            }

            transaction.setProduct(role.getProductMap().get(random.nextInt(role.getProductMapSize())));

            log.info("Start to send purchase request to trader: " + v.getDomain());

            log.info(tradingLaunchService.launchTradingTransaction(transaction));
        });

        return Boolean.TRUE;
    }

}
