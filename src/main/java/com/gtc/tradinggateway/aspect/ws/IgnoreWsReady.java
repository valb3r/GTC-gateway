package com.gtc.tradinggateway.aspect.ws;

import java.lang.annotation.*;

/**
 * Created by Valentyn Berezin on 29.03.18.
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface IgnoreWsReady {
}
