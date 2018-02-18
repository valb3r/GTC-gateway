package com.gtc.tradinggateway.service.hitbtc;

import com.appunite.websocket.rx.object.messages.RxObjectEvent;
import com.appunite.websocket.rx.object.messages.RxObjectEventConnected;
import com.gtc.tradinggateway.config.HitbtcConfig;
import com.gtc.tradinggateway.meta.TradingCurrency;
import com.gtc.tradinggateway.service.BaseWsClient;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rx.Observable;

import static com.gtc.tradinggateway.config.Const.Clients.HITBTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mikro on 18.02.2018.
 */
public class HitbtcWsServiceTest {

    private static final String BASE = "base";
    private static final TradingCurrency from = TradingCurrency.Bitcoin;
    private static final TradingCurrency to = TradingCurrency.Usd;
    private static final double price = 0.2;
    private static final double amount = 0.3;

    @InjectMocks
    private HitbtcWsService hitbtcWsService;

    @Mock
    private HitbtcConfig cfg;

    @Mock
    private RxObjectEventConnected rx;

    @Before
    public void init() {
//        Request request = mock(Request.class);
//        RxObjectEvent evt = mock(RxObjectEvent.class);
//        Observable<RxObjectEvent> obs = Observable.just(evt);
//        when(hitbtcWsService.getConnection(request)).thenReturn(obs);
    }

    @Test
    public void testGetWs() {
        when(cfg.getWsBase()).thenReturn(BASE);

        assertThat(hitbtcWsService.getWs()).isEqualTo(BASE);
    }

    @Test
    public void testName() {
        assertThat(hitbtcWsService.name()).isEqualTo(HITBTC);
    }

    @Test
    public void testHeaders() {
        assertThat(hitbtcWsService.headers().size()).isEqualTo(0);
    }

    @Test
    public void testCreate() {

    }

}
