package com.gtc.tradinggateway.service.binance;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.aspect.rate.RateLimited;
import com.gtc.tradinggateway.config.BinanceConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.binance.dto.*;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.util.CodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.*;

import static com.gtc.tradinggateway.config.Const.Clients.BINANCE;

/**
 * Created by mikro on 23.01.2018.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@RateLimited(ratePerSecond = "${app.binance.ratePerS}", mode = RateLimited.Mode.CLASS)
public class BinanceRestService implements ManageOrders, Withdraw, Account, CreateOrder {

    private static final String ORDERS = "/api/v3/order";
    private static final String ALL_ORDERS = "/api/v3/openOrders";
    private static final String BALANCES = "/api/v3/account";
    private static final String WITHDRAWAL = "/wapi/v3/withdraw.html";

    private final BinanceConfig cfg;
    private final BinanceEncryptionService signer;

    @Override
    public Optional<OrderDto> get(String id) {
        BinanceRequestOrderDto orderDto = new BinanceRequestOrderDto(id);
        String body = orderDto.toString();
        String signedBody = getSignedBody(body);
        ResponseEntity<BinanceGetOrderDto> resp = cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS + "?" + signedBody,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        BinanceGetOrderDto.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            return Optional.of(resp.getBody().mapTo());
        }

        return Optional.empty();
    }

    @SneakyThrows
    private String getSignedBody(String body) {
        return body + "&signature=" + URLEncoder.encode(signer.generate(body), "UTF-8");
    }

    @Override
    public List<OrderDto> getOpen() {
        BinanceRequestDto dto = new BinanceRequestDto();
        String body = dto.toString();
        String signedBody = getSignedBody(body);
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<BinanceGetOrderDto[]> resp = template
                .exchange(
                        cfg.getRestBase() + ALL_ORDERS + "?" + signedBody,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        BinanceGetOrderDto[].class);
        BinanceGetOrderDto[] list = resp.getBody();
        List<OrderDto> result = new ArrayList<>();
        for (BinanceGetOrderDto respDto : list) {
            result.add(respDto.mapTo());
        }
        return result;
    }

    @Override
    public void cancel(String id) {
        BinanceRequestOrderDto orderDto = new BinanceRequestOrderDto(id);
        String body = orderDto.toString();
        String signedBody = getSignedBody(body);
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + ORDERS + "?" + signedBody,
                        HttpMethod.DELETE,
                        new HttpEntity<>(signer.restHeaders()), Object.class);
    }

    @Override
    public Map<TradingCurrency, Double> balances() {
        BinanceRequestDto requestDto = new BinanceRequestDto();
        String body = requestDto.toString();
        String signedBody = getSignedBody(body);
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<BinanceBalanceDto> resp = template
                .exchange(
                        cfg.getRestBase() + BALANCES + "?" + signedBody,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        BinanceBalanceDto.class);
        Map<TradingCurrency, Double> results = new EnumMap<>(TradingCurrency.class);
        BinanceBalanceDto response = resp.getBody();
        BinanceBalanceDto.BinanceBalanceAsset[] assets = response.getBalances();
        for (BinanceBalanceDto.BinanceBalanceAsset asset : assets) {
            CodeMapper.mapAndPut(asset.getCode(), asset.getAmount(), cfg, results);
        }
        return results;
    }

    @Override
    public void withdraw(TradingCurrency currency, double amount, String destination) {
        BinanceRequestDto requestDto = new BinanceWithdrawalRequestDto(currency.toString(), amount, destination);
        String body = requestDto.toString();
        String signedBody = getSignedBody(body);
        cfg.getRestTemplate()
                .exchange(
                        cfg.getRestBase() + WITHDRAWAL + "?" + signedBody,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders()), Object.class);
    }

    @Override
    public OrderCreatedDto create(TradingCurrency from, TradingCurrency to, double amount, double price) {
        Optional<PairSymbol> pair = cfg.fromCurrency(from, to);
        if (!pair.isPresent()) {
            throw new IllegalArgumentException(
                    "Pair from " + from.toString() + " to " + to.toString() + " is not supported");
        }
        PairSymbol pairSym = pair.get();
        if (pairSym.getIsInverted()) {
            amount = -1 / amount;
            price = 1 / price;
        }
        BinancePlaceOrderRequestDto requestDto = new BinancePlaceOrderRequestDto(pairSym, amount, price);
        String body = requestDto.toString();
        String signedBody = getSignedBody(body);
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<BinanceGetOrderDto> resp = template
                .exchange(
                        cfg.getRestBase() + ORDERS + "?" + signedBody,
                        HttpMethod.POST,
                        new HttpEntity<>(signer.restHeaders()),
                        BinanceGetOrderDto.class);
        BinanceGetOrderDto result = resp.getBody();

        return OrderCreatedDto.builder()
                .assignedId(pairSym.toString() + "." + result.getId())
                .build();
    }

    @Override
    @IgnoreRateLimited
    public String name() {
        return BINANCE;
    }
}
