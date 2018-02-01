package com.gtc.tradinggateway.service.binance;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by mikro on 31.01.2018.
 */
public class BinanceRestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException
    {
        HttpHeaders headers = request.getHeaders();
        headers.remove(HttpHeaders.ACCEPT);
        headers.remove(HttpHeaders.CONNECTION);
        headers.remove(HttpHeaders.CONTENT_TYPE);
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        return execution.execute(request, body);
    }

}
