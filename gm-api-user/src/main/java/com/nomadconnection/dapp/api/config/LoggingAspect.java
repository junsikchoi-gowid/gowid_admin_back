package com.nomadconnection.dapp.api.config;

import com.nomadconnection.dapp.api.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final Environment environment;

    @Before("execution(* com.nomadconnection.dapp..controller..*(..))")
    public void beforeController(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        Object[] args = joinPoint.getArgs();

        log.info("########################## Request START ###################################");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("== API : {} ", request.getRequestURI());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("== header > {}: {}", headerName, headerValue);
        }

        // 파라미터를 가져온다
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation paramAnnotation : parameterAnnotations[argIndex]) {
                if (!(paramAnnotation instanceof PathVariable) &&
                        !(paramAnnotation instanceof RequestParam) &&
                        !(paramAnnotation instanceof RequestBody) &&
                        !(paramAnnotation instanceof ModelAttribute)) {
                    continue;
                }
                Parameter parameter = parameters[argIndex];
                Object arg = args[argIndex];

                if (jsonFormatEnable()) {
                    log.info("== param > {}: {}", parameter.getName(), LoggingUtils.getPrettyJsonString(arg));
                } else {
                    log.info("== param > {}: {}", parameter.getName(), arg);
                }
            }
        }

        log.info("########################## Request END ###################################\r\n");


    }

    @AfterReturning(pointcut = "execution(* com.nomadconnection.dapp..controller..*(..))", returning = "result")
    public void afterController(Object result) {
        log.info("########################## Response START ###################################");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("== api > {} ", request.getRequestURI());

        if (result != null) {
            if (jsonFormatEnable()) {
                log.info(" == value > {}", LoggingUtils.getPrettyJsonString(result));
            } else {
                log.info(" == value > {}", result);
            }
        }

        log.info("########################## Response END ###################################\r\n");
    }

    private boolean jsonFormatEnable() {
        String[] envs = environment.getActiveProfiles();
        for (String env : envs) {
            if (env.equals("liv") || env.equals("live") || env.equals("prd") || env.equals("prod")) {
                return false;
            }
        }
        return true;
    }

}
