package com.ecommerce.aspect;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * AOP 日志切面 —— 记录 Controller 层请求参数与执行耗时
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.ecommerce.controller.*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.stream(joinPoint.getArgs())
                .map(arg -> arg == null ? "null" : arg.toString())
                .collect(Collectors.joining(", "));

        long start = System.currentTimeMillis();
        log.info("→ {}.{} 调用 | 参数: [{}]", className, methodName, StrUtil.maxLength(args, 200));

        Object result = joinPoint.proceed();

        long elapsed = System.currentTimeMillis() - start;
        log.info("← {}.{} 返回 | 耗时: {}ms", className, methodName, elapsed);

        return result;
    }
}
