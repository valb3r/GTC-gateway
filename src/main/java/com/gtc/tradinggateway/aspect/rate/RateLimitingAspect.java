package com.gtc.tradinggateway.aspect.rate;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.gtc.tradinggateway.aspect.rate.RateLimited.Mode.CLASS;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@Aspect
@Component
public class RateLimitingAspect {

    private final Map<String, TokenBucket> limiters = new ConcurrentHashMap<>();

    private final EmbeddedValueResolver resolver;

    public RateLimitingAspect(ConfigurableBeanFactory beanFactory) {
        this.resolver = new EmbeddedValueResolver(beanFactory);
    }

    @Around("execution(public * *(..)) && @within(ann) " +
            "&& !@annotation(com.gtc.tradinggateway.aspect.rate.IgnoreRateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited ann) throws Throwable {
        Method method = getMethod(joinPoint);
        String key = getKey(method, ann);
        int tokens = Integer.parseInt(resolver.resolveStringValue(ann.ratePerMinute()));
        boolean acquired = limiters.computeIfAbsent(key, id ->
                        TokenBuckets.builder()
                        .withCapacity(tokens)
                        .withFixedIntervalRefillStrategy(tokens, 1, TimeUnit.MINUTES)
                        .build()
        ).tryConsume();

        if (!acquired) {
            throw new RateTooHighException("Rate limiting");
        }

        return joinPoint.proceed();
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private String getKey(Method method, RateLimited ann) {
        if (CLASS.equals(ann.mode())) {
            return method.getDeclaringClass().getCanonicalName();
        }

        return method.getName();
    }
}
