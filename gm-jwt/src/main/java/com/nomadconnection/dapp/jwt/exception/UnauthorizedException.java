package com.nomadconnection.dapp.jwt.exception;

import com.nomadconnection.dapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
public class UnauthorizedException extends BaseException {

}
