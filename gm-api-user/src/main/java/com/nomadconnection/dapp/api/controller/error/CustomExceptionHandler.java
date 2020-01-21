package com.nomadconnection.dapp.api.controller.error;

import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.dto.response.ErrorResponse;
import com.nomadconnection.dapp.jwt.exception.AccessTokenNotFoundException;
import com.nomadconnection.dapp.jwt.exception.JwtSubjectMismatchedException;
import com.nomadconnection.dapp.jwt.exception.UnacceptableJwtException;
import com.nomadconnection.dapp.api.exception.EmptyResxException;
import com.nomadconnection.dapp.resx.exception.FailedToCreateDirectoriesException;
import com.nomadconnection.dapp.resx.exception.FailedToSaveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
@SuppressWarnings("unused")
public class CustomExceptionHandler {

	//==================================================================================================================
	//
	//	COMMON
	//
	//==================================================================================================================

	@ExceptionHandler(AlreadyExistException.class)
	protected ResponseEntity onAlreadyExistException(AlreadyExistException e) {
		return ResponseEntity.status(e.status()).body(ErrorResponse.from(ErrorCode.Common.ALREADY_EXIST));
	}

//	@ExceptionHandler(AlreadyExistException.class)
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	protected ErrorResponse onAlreadyExistException(AlreadyExistException e) {
//		return ErrorResponse.from(ErrorCode.Common.ALREADY_EXIST);
//	}

	//==================================================================================================================
	//
	//	AUTHENTICATION
	//
	//==================================================================================================================

	@ExceptionHandler(AccessTokenNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onAccessTokenNotFoundException(AccessTokenNotFoundException e) {
		return ErrorResponse.from(ErrorCode.Authentication.ACCESS_TOKEN_NOT_FOUND);
	}

	@ExceptionHandler(UnacceptableJwtException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onUnacceptableJwtException(UnacceptableJwtException e) {
		return ErrorResponse.from(ErrorCode.Authentication.UNACCEPTABLE_JWT_USED);
	}

	@ExceptionHandler(JwtSubjectMismatchedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onJwtSubjectMismatchedException(JwtSubjectMismatchedException e) {
		return ErrorResponse.from(ErrorCode.Authentication.JWT_SUBJECT_MISMATCHED);
	}

	//==================================================================================================================
	//
	//	AUTHORITY
	//
	//==================================================================================================================

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	protected ErrorResponse onUnauthorizedException(UnauthorizedException e) {
		return ErrorResponse.from(ErrorCode.Authority.UNAUHTORIZED);
	}

	@ExceptionHandler(CorpNotRegisteredException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onCorpNotRegisteredException(CorpNotRegisteredException e) {
		return ErrorResponse.from(ErrorCode.Authority.CORP_NOT_REGISTERED);
	}

	@ExceptionHandler(NotAllowedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onNotAllowedException(NotAllowedException e) {
		return ErrorResponse.from(ErrorCode.Authority.NOT_ALLOWED);
	}

	@ExceptionHandler(NotAllowedMemberAuthorityException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onNotAllowedMemberAuthorityException(NotAllowedMemberAuthorityException e) {
		return ErrorResponse.from(ErrorCode.Authority.NOT_ALLOWED_MEMBER_AUTHORITY);
	}

	//==================================================================================================================
	//
	//	MISMATCHED
	//
	//==================================================================================================================

	@ExceptionHandler(MismatchedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onMismatchedException(MismatchedException e) {
		switch (e.category()) {
			case PASSWORD:
				return ErrorResponse.from(ErrorCode.Mismatched.MISMATCHED_PASSWORD);
			case VERIFICATION_CODE:
				return ErrorResponse.from(ErrorCode.Mismatched.MISMATCHED_VERIFICATION_CODE);
			case VALID_THRU:
				return ErrorResponse.from(ErrorCode.Mismatched.MISMATCHED_VALID_THRU);
			case JWT_SUBJECT:
				return ErrorResponse.from(ErrorCode.Mismatched.MISMATCHED_JWT_SUBJECT);
			case CORP:
				return ErrorResponse.from(ErrorCode.Mismatched.MISMATCHED_CORP);
			default:
				return ErrorResponse.from(ErrorCode.Mismatched.MISMATCHED);
		}
	}

	//==================================================================================================================
	//
	//	UNVERIFIED
	//
	//==================================================================================================================

	@ExceptionHandler(UnverifiedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onUnverifiedException(UnverifiedException e) {
		switch (e.resource()) {
			case CVC:
				return ErrorResponse.from(ErrorCode.Unverified.UNVERIFIED_CVC);
			case CVT:
				return ErrorResponse.from(ErrorCode.Unverified.UNVERIFIED_CVT);
		}
		return ErrorResponse.from(ErrorCode.Unverified.UNVERIFIED);
	}

	//==================================================================================================================
	//
	//	RESOURCE
	//
	//==================================================================================================================

	@ExceptionHandler(EmptyResxException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onEmptyResxException(EmptyResxException e) {
		return ErrorResponse.from(ErrorCode.Resource.EMPTY_RESOURCE);
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected ErrorResponse onUserNotFoundException(UserNotFoundException e) {
		return ErrorResponse.from(ErrorCode.Resource.USER_NOT_FOUND);
	}

	@ExceptionHandler(FailedToCreateDirectoriesException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected ErrorResponse onFailedToCreateDirectoriesException(FailedToCreateDirectoriesException e) {
		return ErrorResponse.from(ErrorCode.Resource.FAILED_TO_CREATE_DIRECTORIES);
	}

	@ExceptionHandler(FailedToSaveException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected ErrorResponse onFailedToSaveException(FailedToSaveException e) {
		return ErrorResponse.from(ErrorCode.Resource.FAILED_TO_SAVE);
	}

	@ExceptionHandler(DeptNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse onDeptNotFoundException(DeptNotFoundException e) {
		return ErrorResponse.from(ErrorCode.Resource.DEPT_NOT_FOUND);
	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.OK)
	protected ErrorResponse onBusinessException(BusinessException e) {
		return ErrorResponse.from(e.getError(), e.getDescription());
	}
}
