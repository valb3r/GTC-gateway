package com.gtc.tradinggateway.service.binance;

import com.gtc.tradinggateway.BaseMockitoTest;
import com.gtc.tradinggateway.config.BinanceConfig;
import com.gtc.tradinggateway.service.binance.dto.BinanceGetOrderDto;
import com.gtc.tradinggateway.service.dto.OrderDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Valentyn Berezin on 03.02.18.
 */
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
}
