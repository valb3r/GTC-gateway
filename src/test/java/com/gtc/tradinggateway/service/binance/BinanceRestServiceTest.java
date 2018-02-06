package com.gtc.tradinggateway.service.binance;

import com.gtc.tradinggateway.BaseMockitoTest;
import com.gtc.tradinggateway.config.BinanceConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.binance.dto.BinanceBalanceDto;
import com.gtc.tradinggateway.service.binance.dto.BinanceGetOrderDto;
import com.gtc.tradinggateway.service.binance.dto.BinanceRequestDto;
import com.gtc.tradinggateway.service.dto.OrderDto;
import lombok.extern.log4j.Log4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Valentyn Berezin on 03.02.18.
 */
@Log4j
public class BinanceRestServiceTest extends BaseMockitoTest {

    private static final String ASSIGNED_ID = "1234";
    private static final String SYMBOL = "BTC-USD";
    private static final String ID = SYMBOL + "." + ASSIGNED_ID;
    private static final String BASE = "base";
    private static final String SIGNED = "signed";

    @Mock
    private BinanceConfig cfg;

    @Mock
    private BinanceEncryptionService signer;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity> entity;

    @Captor
    private ArgumentCaptor<String> signedCaptor;

    @Captor
    private ArgumentCaptor<String> requestCaptor;

    @InjectMocks
    private BinanceRestService binanceRestService;

    @Before
    public void init() {
        when(cfg.getRestTemplate()).thenReturn(restTemplate);
        when(cfg.getRestBase()).thenReturn(BASE);
        HttpHeaders headers = new HttpHeaders();
        when(signer.generate(signedCaptor.capture())).thenReturn(SIGNED);
        when(signer.restHeaders()).thenReturn(headers);
    }

    @Test
    public void testGetReturnOrder() {
        BinanceGetOrderDto orderGet = mock(BinanceGetOrderDto.class);
        OrderDto expectedOrder = mock(OrderDto.class);
        when(orderGet.mapTo()).thenReturn(expectedOrder);
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.GET),
                entity.capture(),
                eq(BinanceGetOrderDto.class)

        )).thenReturn(new ResponseEntity<>(orderGet, HttpStatus.OK));

        Optional<OrderDto> result = binanceRestService.get(ID);


        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(expectedOrder);
        assertThat(requestCaptor.getValue()).startsWith(BASE + "/api/v3/order?symbol=" + SYMBOL
                + "&orderId=" + ASSIGNED_ID);
        assertThat(requestCaptor.getValue()).endsWith("signature=" + SIGNED);
        assertThat(requestCaptor.getValue()).contains("timestamp=");
        assertThat(signedCaptor.getValue()).startsWith("symbol=" + SYMBOL + "&orderId=" + ASSIGNED_ID);
        assertThat(signedCaptor.getValue()).contains("timestamp=");
    }

    @Test
    public void testGetAllOpenOrders() {
        BinanceGetOrderDto orderGet = mock(BinanceGetOrderDto.class);
        OrderDto expectedOrder = mock(OrderDto.class);
        when(orderGet.mapTo()).thenReturn(expectedOrder);
        BinanceGetOrderDto[] response = new BinanceGetOrderDto[1];
        response[0] = orderGet;
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.GET),
                entity.capture(),
                eq(BinanceGetOrderDto[].class)

        )).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        List<OrderDto> result = binanceRestService.getOpen();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(expectedOrder);
        assertThat(requestCaptor.getValue()).startsWith(BASE + "/api/v3/openOrders?");
        assertThat(requestCaptor.getValue()).endsWith("signature=" + SIGNED);
        assertThat(requestCaptor.getValue()).contains("timestamp=");
        assertThat(signedCaptor.getValue()).contains("timestamp=");
    }

    @Test
    public void testCancelOrder() {
        Object orderGet = mock(Object.class);
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.DELETE),
                entity.capture(),
                eq(Object.class)

        )).thenReturn(new ResponseEntity<>(orderGet, HttpStatus.OK));

        binanceRestService.cancel(ID);

        assertThat(requestCaptor.getValue()).startsWith(BASE + "/api/v3/order?symbol=" + SYMBOL
                + "&orderId=" + ASSIGNED_ID);
        assertThat(requestCaptor.getValue()).endsWith("signature=" + SIGNED);
        assertThat(requestCaptor.getValue()).contains("timestamp=");
        assertThat(signedCaptor.getValue()).startsWith("symbol=" + SYMBOL + "&orderId=" + ASSIGNED_ID);
        assertThat(signedCaptor.getValue()).contains("timestamp=");

    }

    @Test
    public void testGetBalances() {
        BinanceBalanceDto orderGet = new BinanceBalanceDto();
        BinanceBalanceDto.BinanceBalanceAsset balanceItem = mock(BinanceBalanceDto.BinanceBalanceAsset.class);
        BinanceBalanceDto.BinanceBalanceAsset invalidBalanceItem = mock(BinanceBalanceDto.BinanceBalanceAsset.class);
        BinanceBalanceDto.BinanceBalanceAsset[] balances = new BinanceBalanceDto.BinanceBalanceAsset[2];
        balanceItem.setCode("BTC");
        balanceItem.setAmount(0.1);
        invalidBalanceItem.setCode("XXX");
        invalidBalanceItem.setAmount(0.1);
        balances[0] = balanceItem;
        balances[1] = invalidBalanceItem;
        orderGet.setBalances(balances);

        log.info(orderGet.toString());

        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.GET),
                entity.capture(),
                eq(BinanceBalanceDto.class)

        )).thenReturn(new ResponseEntity<>(orderGet, HttpStatus.OK));

        Map<TradingCurrency, Double> results = binanceRestService.balances();

        log.info(results.toString());

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(TradingCurrency.Bitcoin)).isEqualTo(0.1);
        assertThat(requestCaptor.getValue()).startsWith(BASE + "/api/v3/account?");
        assertThat(requestCaptor.getValue()).endsWith("signature=" + SIGNED);
        assertThat(requestCaptor.getValue()).contains("timestamp=");
        assertThat(signedCaptor.getValue()).contains("timestamp=");

    }
}
