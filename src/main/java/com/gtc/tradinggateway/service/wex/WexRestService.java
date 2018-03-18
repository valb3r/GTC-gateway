package com.gtc.tradinggateway.service.wex;

import com.google.common.annotations.VisibleForTesting;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.aspect.rate.RateLimited;
import com.gtc.tradinggateway.config.WexConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.service.wex.dto.*;
import com.gtc.tradinggateway.util.CodeMapper;
import com.gtc.tradinggateway.util.DefaultInvertHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gtc.tradinggateway.config.Const.Clients.WEX;

/**
 * Done orders will appear in /OrderInfo (no need for extra check).
 * Validated basic functionality (create, get, get all, cancel)
 * 05.03.2018
 */
@Slf4j
@Service
@RequiredArgsConstructor
@RateLimited(ratePerMinute = "${app.wex.ratePerM}", mode = RateLimited.Mode.CLASS)
public class WexRestService implements ManageOrders, Withdraw, Account, CreateOrder {

    private static final long NONCE_BEGIN = 1520154667151L;

    private static final String BALANCES = "getInfo";
    private static final String CREATE = "Trade";
    private static final String GET = "OrderInfo";
    private static final String GET_OPEN = "ActiveOrders";
    private static final String CANCEL = "CancelOrder";
    private static final String WITHDRAW = "WithdrawCoin";

    private final WexConfig cfg;
    private final WexEncryptionService signer;

    @Override
    @SneakyThrows
    public Map<TradingCurrency, BigDecimal> balances() {
        BaseWexRequest request = new BaseWexRequest(nonce(), BALANCES);
        ResponseEntity<WexBalancesDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase(),
                        HttpMethod.POST,
                        new HttpEntity<>(request, signer.sign(request)),
                        WexBalancesDto.class);

        resp.getBody().selfAssert();

        Map<TradingCurrency, BigDecimal> results = new EnumMap<>(TradingCurrency.class);
        WexBalancesDto.Value value = resp.getBody().getRet();
        value.getFunds().forEach((key, amount) ->
                CodeMapper.mapAndPut(key, amount, cfg, results)
        );

        return results;
    }

    @Override
    public Optional<OrderCreatedDto> create(String tryToAssignId, TradingCurrency from, TradingCurrency to,
                                            BigDecimal amount, BigDecimal price) {
        PairSymbol pair = cfg.pairFromCurrency(from, to)
                .orElseThrow(() -> new IllegalStateException("Unsupported pair"));
        BigDecimal calcAmount = DefaultInvertHandler.amountFromOrig(pair, amount, price);
        BigDecimal calcPrice = DefaultInvertHandler.priceFromOrig(pair, price);
        WexCreateOrder request = new WexCreateOrder(nonce(), CREATE, pair.getSymbol(),
                DefaultInvertHandler.amountToBuyOrSell(calcAmount),
                calcPrice,
                calcAmount.abs()
        );

        ResponseEntity<WexCreateResponse> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase(),
                        HttpMethod.POST,
                        new HttpEntity<>(request, signer.sign(request)),
                        WexCreateResponse.class);

        resp.getBody().selfAssert();

        return Optional.of(
                OrderCreatedDto.builder()
                        .assignedId(String.valueOf(resp.getBody().getRet().getOrderId()))
                        .isExecuted(0 == resp.getBody().getRet().getOrderId())
                        .build()
        );
    }

    @Override
    public Optional<OrderDto> get(String id) {
        WexGetRequest request = new WexGetRequest(nonce(), GET, Long.valueOf(id));
        ResponseEntity<WexGetResponse> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase(),
                        HttpMethod.POST,
                        new HttpEntity<>(request, signer.sign(request)),
                        WexGetResponse.class);

        resp.getBody().selfAssert();

        return resp.getBody().mapTo();
    }

    @Override
    public List<OrderDto> getOpen() {
        WexGetOpenRequest request = new WexGetOpenRequest(nonce(), GET_OPEN);
        ResponseEntity<WexGetOpenResponse> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase(),
                        HttpMethod.POST,
                        new HttpEntity<>(request, signer.sign(request)),
                        WexGetOpenResponse.class);

        resp.getBody().selfAssert();

        return resp.getBody().mapTo();
    }

    @Override
    public void cancel(String id) {
        WexCancelOrderRequest request = new WexCancelOrderRequest(nonce(), CANCEL, Long.valueOf(id));
        ResponseEntity<WexCancelOrderResponse> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase(),
                        HttpMethod.POST,
                        new HttpEntity<>(request, signer.sign(request)),
                        WexCancelOrderResponse.class);

        resp.getBody().selfAssert();

        log.info("Cancel request completed {}", resp.getBody());
    }

    @Override
    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {
        // NOTE: WEX requires special API key permissions for doing that
        WexWithdrawRequest request = new WexWithdrawRequest(
                nonce(),
                WITHDRAW,
                cfg.getCustomResponseCurrencyMapping()
                        .getOrDefault(currency.getCode(), currency.getCode()).toUpperCase(),
                amount,
                destination
        );

        ResponseEntity<WexWithdrawResponse> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase(),
                        HttpMethod.POST,
                        new HttpEntity<>(request, signer.sign(request)),
                        WexWithdrawResponse.class);

        resp.getBody().selfAssert();

        log.info("Withdraw request completed {}", resp.getBody());
    }

    @Override
    @IgnoreRateLimited
    public String name() {
        return WEX;
    }

    // sufficient for 9940 days and rate 10 r/s - it has max of 2^32 as per docs
    @VisibleForTesting
    protected int nonce() {
        return (int) ((System.currentTimeMillis() - NONCE_BEGIN) / 100);
    }
}
