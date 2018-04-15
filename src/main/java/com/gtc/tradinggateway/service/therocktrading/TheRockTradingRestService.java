package com.gtc.tradinggateway.service.therocktrading;

import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.aspect.rate.RateLimited;
import com.gtc.tradinggateway.config.TheRockTradingConfig;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.service.therocktrading.dto.TheRockTradingBalanceResponseDto;
import com.gtc.tradinggateway.service.therocktrading.dto.TheRockTradingCreateRequestDto;
import com.gtc.tradinggateway.service.therocktrading.dto.TheRockTradingCreateResponseDto;
import com.gtc.tradinggateway.service.therocktrading.dto.TheRockTradingOrderDto;
import com.gtc.tradinggateway.util.CodeMapper;
import com.gtc.tradinggateway.util.DefaultInvertHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.gtc.tradinggateway.config.Const.Clients.THEROCKTRADING;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
@RateLimited(ratePerMinute = "${app.therocktrading.ratePerM}", mode = RateLimited.Mode.CLASS)
public class TheRockTradingRestService implements Account, CreateOrder, ManageOrders, Withdraw {

    private static String BALANCE = "/v1/balances";
    private static String FUNDS = "/v1/funds";
    private static String ORDERS = "/orders";

    private final TheRockTradingConfig cfg;
    private final TheRockTradingEncryptionService signer;

    public Map<TradingCurrency, BigDecimal> balances() {
        String url = cfg.getRestBase() + BALANCE;
        ResponseEntity<TheRockTradingBalanceResponseDto> resp = cfg.getRestTemplate()
                .exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders(url)),
                        TheRockTradingBalanceResponseDto.class);
        Map<TradingCurrency, BigDecimal> results = new EnumMap<>(TradingCurrency.class);
        TheRockTradingBalanceResponseDto response = resp.getBody();
        List<TheRockTradingBalanceResponseDto.BalanceItem> assets = response.getBalances();
        for (TheRockTradingBalanceResponseDto.BalanceItem asset : assets) {
            CodeMapper.mapAndPut(asset.getCurrency(), asset.getBalance(), cfg, results);
        }
        return results;
    }

    public Optional<OrderCreatedDto> create(String tryToAssignId,
                                            TradingCurrency from,
                                            TradingCurrency to,
                                            BigDecimal amount,
                                            BigDecimal price
    ) {
        PairSymbol pair = cfg.pairFromCurrency(from, to).orElseThrow(() -> new IllegalArgumentException(
                "Pair from " + from.toString() + " to " + to.toString() + " is not supported")
        );

        BigDecimal calcAmount = DefaultInvertHandler.amountFromOrig(pair, amount, price);
        BigDecimal calcPrice = DefaultInvertHandler.priceFromOrig(pair, price);

        TheRockTradingCreateRequestDto requestDto = new TheRockTradingCreateRequestDto(
                pair.toString(),
                DefaultInvertHandler.amountToBuyOrSell(calcAmount),
                calcAmount.abs().toString(),
                calcPrice.toString()
        );

        String url = cfg.getRestBase() + FUNDS + "/" + pair.toString() + ORDERS;

        ResponseEntity<TheRockTradingCreateResponseDto> resp = cfg.getRestTemplate()
                .exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(requestDto, signer.restHeaders(url)),
                        TheRockTradingCreateResponseDto.class);

        return Optional.of(resp.getBody().getOrder().mapToCreate());
    }

    public Optional<OrderDto> get(String id) {
        String[] idSplitted = id.split(".");
        String pair = idSplitted[0];
        String realId = idSplitted[1];

        String url = cfg.getRestBase() + FUNDS + "/" + pair + ORDERS + "/" + realId;

        ResponseEntity<TheRockTradingOrderDto> resp = cfg.getRestTemplate()
                .exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders(url)),
                        TheRockTradingOrderDto.class);

        return Optional.of(resp.getBody().mapTo());
    }

    public List<OrderDto> getOpen() {
        return new ArrayList<>();
    }

    public void cancel(String id) {

    }

    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {

    }

    @IgnoreRateLimited
    public String name() {
        return THEROCKTRADING;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 150000)
    public void ttt() {
        create("123",
                TradingCurrency.Bitcoin,
                TradingCurrency.Litecoin,
                new BigDecimal(1),
                new BigDecimal(1));
    }

}
