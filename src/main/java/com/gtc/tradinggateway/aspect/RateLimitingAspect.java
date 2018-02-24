package com.gtc.tradinggateway.aspect;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.gtc.tradinggateway.aspect.RateLimited.Mode.CLASS;

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

    @Around("execution(public * *(..)) && @within(ann) " +
            "&& !@annotation(com.gtc.tradinggateway.aspect.IgnoreRateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited ann) throws Throwable {
        Method method = getMethod(joinPoint);
        String key = getKey(method, ann);

        boolean acquired = limiters.computeIfAbsent(key, id -> RateLimiter.create(
                Double.valueOf(resolver.resolveStringValue(ann.ratePerSecond()))
        )).tryAcquire();

        if (!acquired) {
            throw new IllegalStateException("Rate limiting");
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
