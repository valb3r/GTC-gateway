package com.gtc.tradinggateway.service;

/**
 * Created by mikro on 20.02.2018.
 */
public interface CancelOrder extends ClientNamed {

    void cancel(String id);
}
