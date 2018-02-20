package com.gtc.tradinggateway.aspect;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Valentyn Berezin on 20.02.18.
 */
@Aspect
@Component
public class RateLimitingAspect {

    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    private final EmbeddedValueResolver resolver;

    public RateLimitingAspect(ConfigurableBeanFactory beanFactory) {
        this.resolver = new EmbeddedValueResolver(beanFactory);
    }

    @Around("@annotation(com.gtc.tradinggateway.aspect.RateLimited) || @within(com.gtc.tradinggateway.aspect.RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        String key = method.toGenericString();
        limiters.computeIfAbsent(key, id -> RateLimiter.create(getRate(method))).acquire();
        return joinPoint.proceed();
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private double getRate(Method method) {
        RateLimited rateSpel = Optional.ofNullable(AnnotationUtils.getAnnotation(method, RateLimited.class))
                .orElse(AnnotationUtils.getAnnotation(method.getDeclaringClass(), RateLimited.class));
        return Double.valueOf(resolver.resolveStringValue(rateSpel.ratePerSecond()));
    }
}
