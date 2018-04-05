package com.gtc.tradinggateway.service.huobi;

import com.google.common.base.Charsets;
import com.gtc.model.tradinggateway.api.dto.data.OrderDto;
import com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited;
import com.gtc.tradinggateway.config.HuobiConfig;
import com.gtc.tradinggateway.config.converters.FormHttpMessageToPojoConverter;
import com.gtc.tradinggateway.meta.PairSymbol;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.Account;
import com.gtc.tradinggateway.service.CreateOrder;
import com.gtc.tradinggateway.service.ManageOrders;
import com.gtc.tradinggateway.service.Withdraw;
import com.gtc.tradinggateway.service.dto.OrderCreatedDto;
import com.gtc.tradinggateway.service.huobi.dto.*;
import com.gtc.tradinggateway.util.DefaultInvertHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

import static com.gtc.tradinggateway.config.Const.Clients.HUOBI;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class HuobiRestService implements ManageOrders, Withdraw, Account, CreateOrder {

    private final HuobiConfig cfg;
    private final HuobiEncryptionService signer;

    private static String ORDERS = "/v1/order/orders";
    private static String CREATE_ORDER = ORDERS + "/place";
    private static String CANCEL_ORDER = "/submitcancel";
    private static String WITHDRAWAL = "/v1/dw/withdraw/api/create";

    @Override
    public Optional<OrderCreatedDto> create(String tryToAssignId, TradingCurrency from, TradingCurrency to,
                                            BigDecimal amount, BigDecimal price) {
        PairSymbol pair = cfg.pairFromCurrency(from, to).orElseThrow(() -> new IllegalArgumentException(
                "Pair from " + from.toString() + " to " + to.toString() + " is not supported")
        );
        BigDecimal calcAmount = DefaultInvertHandler.amountFromOrig(pair, amount, price);
        BigDecimal calcPrice = DefaultInvertHandler.priceFromOrig(pair, price);
        HuobiCreateRequestDto dto = new HuobiCreateRequestDto(
                DefaultInvertHandler.amountToBuyOrSell(calcAmount) + "-limit",
                getAccountId(),
                calcAmount.abs(),
                calcPrice,
                pair.toString());
        HuobiRequestDto requestDto = new HuobiRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<HuobiCreateResponseDto> resp = template
                .exchange(
                        getQueryUri(HttpMethod.POST, CREATE_ORDER, requestDto),
                        HttpMethod.POST,
                        new HttpEntity<>(dto, signer.restHeaders(HttpMethod.POST)),
                        HuobiCreateResponseDto.class
                );
        return Optional.of(
                OrderCreatedDto.builder()
                        .assignedId(resp.getBody().getOrderId())
                        .build());
    }

    @Override
    public Optional<OrderDto> get(String id) {
        HuobiGetOrderRequestDto requestDto = new HuobiGetOrderRequestDto(cfg.getPublicKey(), id);
        RestTemplate template = cfg.getRestTemplate();
        ResponseEntity<HuobiGetResponseDto> resp = template
                .exchange(
                        getQueryUri(HttpMethod.GET, ORDERS + "/" + id, requestDto),
                        HttpMethod.GET,
                        new HttpEntity<>(signer.restHeaders()),
                        HuobiGetResponseDto.class);
        return Optional.of(resp.getBody()
                .getOrder()
                .mapTo());
    }

    @Override
    @SneakyThrows
    public List<OrderDto> getOpen() {
        throw new Exception("Not implemented");
    }

    @Override
    public void cancel(String id) {
        HuobiRequestDto requestDto = new HuobiRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();
        template.exchange(
                getQueryUri(HttpMethod.POST, ORDERS + "/" + id + CANCEL_ORDER, requestDto),
                HttpMethod.POST,
                new HttpEntity<>(signer.restHeaders(HttpMethod.POST)),
                Object.class);
    }

    @Override
    public Map<TradingCurrency, BigDecimal> balances() {
        HuobiRequestDto requestDto = new HuobiRequestDto(cfg.getPublicKey());
        RestTemplate template = cfg.getRestTemplate();

        return new HashMap<>();
    }

    @Override
    public void withdraw(TradingCurrency currency, BigDecimal amount, String destination) {
        HuobiRequestDto dto = new HuobiRequestDto(cfg.getPublicKey());
        HuobiWithdrawalRequestDto requestDto = new HuobiWithdrawalRequestDto(
                destination,
                amount.toString(),
                currency.toString().toLowerCase());
        RestTemplate template = cfg.getRestTemplate();
        template.exchange(
                getQueryUri(HttpMethod.POST, WITHDRAWAL, dto),
                HttpMethod.POST,
                new HttpEntity<>(requestDto, signer.restHeaders(HttpMethod.POST)),
                Object.class);
    }

    @Override
    @IgnoreRateLimited
    public String name() {
        return HUOBI;
    }

    @SneakyThrows
    private URI getQueryUri(HttpMethod method, String path, Object queryObj) {
        // HUOBI USES FORM ENCODING IN QUERY !
        String query = FormHttpMessageToPojoConverter.pojoSerialize(cfg.getMapper(), queryObj, null);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(cfg.getRestBase())
                .path(path);
        Arrays.stream(query.split("&")).map(it -> it.split("=")).forEach(it -> builder.queryParam(it[0], it[1]));
        builder.queryParam(
                "Signature",
                URLEncoder.encode(signer.generate(method, path, query), Charsets.UTF_8.name())
        );
        return builder.build(true).toUri();
    }

    private String getAccountId() {
        return "test";
    }

    @IgnoreRateLimited
    @Scheduled(initialDelay = 0, fixedDelay = 10000000)
    public void ttt() {
        withdraw(TradingCurrency.Bitcoin, new BigDecimal(1), "0x0000");
    }
}
