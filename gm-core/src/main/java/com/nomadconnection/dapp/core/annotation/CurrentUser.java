package com.nomadconnection.dapp.core.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({
		ElementType.TYPE,
		ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
@SuppressWarnings("unused")
public @interface CurrentUser {

}
