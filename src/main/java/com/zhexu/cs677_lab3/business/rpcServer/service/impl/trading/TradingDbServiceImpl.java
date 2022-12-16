package com.zhexu.cs677_lab3.business.rpcServer.service.impl.trading;

import com.zhexu.cs677_lab3.api.bean.Role;
import com.zhexu.cs677_lab3.api.bean.basic.Product;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.MarketTransaction;
import com.zhexu.cs677_lab3.api.bean.basic.dataEntities.Stock;
import com.zhexu.cs677_lab3.api.bean.basic.factories.SingletonFactory;
import com.zhexu.cs677_lab3.api.bean.response.MarketTransactionResultResponse;
import com.zhexu.cs677_lab3.api.repository.CouchDBCURDForStock;
import com.zhexu.cs677_lab3.api.repository.impl.CouchDBCURDForTransactionRecordsImpl;
import com.zhexu.cs677_lab3.business.rpcServer.service.trading.TradingDbService;
import com.zhexu.cs677_lab3.constants.ResponseCode;
import com.zhexu.cs677_lab3.utils.SpringContextUtils;

import java.util.Map;

import static com.zhexu.cs677_lab3.constants.Consts.STORE_FAIL_MESSAGE;
import static com.zhexu.cs677_lab3.constants.Consts.STORE_SUCCESS_MESSAGE;

/**
 * @project: COMPSCI677_LAB3
 * @description:
 * @author: zhexu
 * @create: 12/16/22
 **/
public class TradingDbServiceImpl implements TradingDbService {
    Role role = SingletonFactory.getRole();
    @Override
    public MarketTransactionResultResponse tradDb(MarketTransaction transaction) {
        MarketTransactionResultResponse resultResponse = new MarketTransactionResultResponse();
        CouchDBCURDForStock dbCurdForStock = SpringContextUtils.getBean(CouchDBCURDForStock.class);
        CouchDBCURDForTransactionRecordsImpl dbCurdForTransactionRecords = SpringContextUtils.getBean(CouchDBCURDForTransactionRecordsImpl.class);

        if(null == transaction.getSeller()){

            for(String k: role.getStockMap().keySet()){
                Stock dbStockItem = dbCurdForStock.getStockById(k);
                Map<Product, Integer> stockMap = dbStockItem.getStock();
                Integer result = stockMap.get(transaction.getProduct()) - transaction.getNumber();
                if(result < 0){
                    continue;
                }
                return getMarketTransactionResultResponse(transaction, resultResponse, dbCurdForStock, dbCurdForTransactionRecords, dbStockItem, stockMap, result);
            }

            resultResponse.setStatus(ResponseCode.STATUS_FORBIDDEN);
            resultResponse.setMessage(STORE_FAIL_MESSAGE);
            transaction.setSuccessful(Boolean.FALSE);
            transaction.setRemark("Stock insufficient!");
            resultResponse.setTransaction(transaction);
            dbCurdForTransactionRecords.add(transaction);
            return resultResponse;

        } else {
            Stock dbStockItem = dbCurdForStock.getStockById(transaction.getSeller().toString());
            Map<Product, Integer> stockMap = dbStockItem.getStock();
            Integer result = stockMap.get(transaction.getProduct()) - transaction.getNumber();
            return getMarketTransactionResultResponse(transaction, resultResponse, dbCurdForStock, dbCurdForTransactionRecords, dbStockItem, stockMap, result);
        }
    }

    private MarketTransactionResultResponse getMarketTransactionResultResponse(MarketTransaction transaction, MarketTransactionResultResponse resultResponse, CouchDBCURDForStock dbCurdForStock, CouchDBCURDForTransactionRecordsImpl dbCurdForTransactionRecords, Stock dbStockItem, Map<Product, Integer> stockMap, Integer result) {
        stockMap.put(transaction.getProduct(), result);
        dbStockItem.setStock(stockMap);
        dbCurdForStock.updateStock(dbStockItem);
        resultResponse.setMessage(STORE_SUCCESS_MESSAGE);
        transaction.setSuccessful(Boolean.TRUE);
        resultResponse.setTransaction(transaction);
        dbCurdForTransactionRecords.add(transaction);
        return resultResponse;
    }
}
