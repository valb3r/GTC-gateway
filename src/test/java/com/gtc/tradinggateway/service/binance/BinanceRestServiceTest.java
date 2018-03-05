package com.gtc.tradinggateway.service.binance;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.BaseMockitoTest;
import com.gtc.tradinggateway.config.BinanceConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.binance.dto.BinanceBalanceDto;
import com.gtc.tradinggateway.service.binance.dto.BinanceGetOrderDto;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Mock
    private BinanceGetOrderDto getOrderDto;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BinanceBalanceDto balanceDto;

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
        OrderDto expectedOrder = mock(OrderDto.class);
        when(getOrderDto.mapTo()).thenReturn(expectedOrder);
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.GET),
                entity.capture(),
                eq(BinanceGetOrderDto.class)

        )).thenReturn(new ResponseEntity<>(getOrderDto, HttpStatus.OK));

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
        OrderDto expectedOrder = mock(OrderDto.class);
        when(getOrderDto.mapTo()).thenReturn(expectedOrder);
        BinanceGetOrderDto[] response = new BinanceGetOrderDto[1];
        response[0] = getOrderDto;
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
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.DELETE),
                entity.capture(),
                eq(Object.class)

        )).thenReturn(new ResponseEntity<>(new Object(), HttpStatus.OK));

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
        BinanceBalanceDto.BinanceBalanceAsset balanceItem = mock(BinanceBalanceDto.BinanceBalanceAsset.class);
        BinanceBalanceDto.BinanceBalanceAsset invalidBalanceItem = mock(BinanceBalanceDto.BinanceBalanceAsset.class);
        BigDecimal amount = BigDecimal.valueOf(0.1);
        when(balanceDto.getBalances()).thenReturn(new BinanceBalanceDto.BinanceBalanceAsset[]{balanceItem, invalidBalanceItem});
        when(balanceItem.getCode()).thenReturn(TradingCurrency.Bitcoin.toString());
        when(balanceItem.getAmount()).thenReturn(amount);
        when(invalidBalanceItem.getCode()).thenReturn("XXX");
        when(invalidBalanceItem.getAmount()).thenReturn(BigDecimal.valueOf(0.1));

        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.GET),
                entity.capture(),
                eq(BinanceBalanceDto.class)
        )).thenReturn(new ResponseEntity<>(balanceDto, HttpStatus.OK));

        Map<TradingCurrency, BigDecimal> results = binanceRestService.balances();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(TradingCurrency.Bitcoin)).isEqualTo(amount);
        assertThat(requestCaptor.getValue()).startsWith(BASE + "/api/v3/account?");
        assertThat(requestCaptor.getValue()).endsWith("signature=" + SIGNED);
        assertThat(requestCaptor.getValue()).contains("timestamp=");
        assertThat(signedCaptor.getValue()).contains("timestamp=");
    }

    @Test
    public void testWithdraw() {
        String destination = "0x0001";
        BigDecimal amount = BigDecimal.valueOf(0.1);
        TradingCurrency currency = TradingCurrency.Bitcoin;
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.POST),
                entity.capture(),
                eq(Object.class)

        )).thenReturn(new ResponseEntity<>(new Object(), HttpStatus.OK));

        binanceRestService.withdraw(currency, amount, destination);

        assertThat(requestCaptor.getValue()).startsWith(BASE + "/wapi/v3/withdraw.html?asset=" +
                currency + "&address=" + destination + "&amountFromOrig=" + amount);
        assertThat(requestCaptor.getValue()).endsWith("signature=" + SIGNED);
        assertThat(requestCaptor.getValue()).contains("timestamp=");
        assertThat(signedCaptor.getValue()).contains("timestamp=");

    }

    @Test
    public void testCreateOrder() {
        PairSymbol pairSym = mock(PairSymbol.class);
        Optional<PairSymbol> pair = Optional.of(pairSym);
        TradingCurrency from = TradingCurrency.Bitcoin;
        TradingCurrency to = TradingCurrency.Usd;
        BigDecimal amount = BigDecimal.valueOf(0.1);
        BigDecimal price = BigDecimal.valueOf(0.2);
        String id = "testid";
        when(getOrderDto.getId()).thenReturn(id);
        when(cfg.pairFromCurrency(from, to)).thenReturn(pair);
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.POST),
                entity.capture(),
                eq(BinanceGetOrderDto.class)

        )).thenReturn(new ResponseEntity<>(getOrderDto, HttpStatus.OK));

        OrderCreatedDto result = binanceRestService.create(from, to, amount, price);

        assertThat(result.getAssignedId()).isEqualTo(pairSym.toString() + "." + id);
        assertThat(requestCaptor.getValue()).startsWith(BASE + "/api/v3/order?symbol=" + pairSym +
            "&side=BUY&type=LIMIT&timeInForce=GTC&quantity=" + amount + "&priceFromOrig=" +
                price + "&recvWindow=5000");
        assertThat(requestCaptor.getValue()).endsWith("signature=" + SIGNED);
        assertThat(requestCaptor.getValue()).contains("timestamp=");
        assertThat(signedCaptor.getValue()).contains("timestamp=");
    }

    @Test
    public void testCreateInvertedOrder() {
        PairSymbol pairSym = mock(PairSymbol.class);
        Optional<PairSymbol> pair = Optional.of(pairSym);
        TradingCurrency from = TradingCurrency.Bitcoin;
        TradingCurrency to = TradingCurrency.Usd;
        BigDecimal amount = BigDecimal.valueOf(0.1);
        BigDecimal price = BigDecimal.valueOf(0.2);
        String id = "testid";
        when(getOrderDto.getId()).thenReturn(id);
        when(cfg.pairFromCurrency(from, to)).thenReturn(pair);
        when(pairSym.getIsInverted()).thenReturn(true);
        when(restTemplate.exchange(
                requestCaptor.capture(),
                eq(HttpMethod.POST),
                entity.capture(),
                eq(BinanceGetOrderDto.class)
        )).thenReturn(new ResponseEntity<>(getOrderDto, HttpStatus.OK));

        binanceRestService.create(from, to, amount, price);

        assertThat(requestCaptor.getValue()).startsWith(BASE + "/api/v3/order?symbol=" + pairSym
                + "&side=SELL&type=LIMIT&timeInForce=GTC&quantity="
                + amount.negate().multiply(price).abs() + "&priceFromOrig="
                + BigDecimal.ONE.divide(price, RoundingMode.HALF_EVEN) + "&recvWindow=5000"
        );
    }
}
