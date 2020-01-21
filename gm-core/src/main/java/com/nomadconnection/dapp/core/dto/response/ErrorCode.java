package com.nomadconnection.dapp.core.dto.response;

@SuppressWarnings("unused")
public class ErrorCode {

	public enum Regular implements ErrorCodeDescriptor {
		USERNAME_NOT_FOUND,
		METHOD_ARGUMENT_NOT_VALID,
		PROPERTY_REFERENCE_ERROR,
		CONSTRAINT_VIOLATION,
		IO_EXCEPTION,
	}

	public enum Common implements ErrorCodeDescriptor {
		ALREADY_EXIST,
	}

	public enum Authentication implements ErrorCodeDescriptor {
		ACCESS_TOKEN_NOT_FOUND,
		UNACCEPTABLE_JWT_USED,
		JWT_SUBJECT_MISMATCHED,
	}

	public enum Authority implements ErrorCodeDescriptor {
		UNAUHTORIZED,
		CORP_NOT_REGISTERED,
		NOT_ALLOWED,
		NOT_ALLOWED_MEMBER_AUTHORITY
	}

	public enum Request implements ErrorCodeDescriptor {
		INVALID_PARAMETER,
	}

	public enum Resource implements ErrorCodeDescriptor {
		EMPTY_RESOURCE,
		FAILED_TO_CREATE_DIRECTORIES,
		FAILED_TO_SAVE,
		USER_NOT_FOUND,
		DEPT_NOT_FOUND,
	}

	public enum Mismatched implements ErrorCodeDescriptor {
		MISMATCHED,
		MISMATCHED_PASSWORD,
		MISMATCHED_JWT_SUBJECT,
		MISMATCHED_VERIFICATION_CODE,
		MISMATCHED_VALID_THRU,
		MISMATCHED_CORP,
	}

	public enum Unverified implements ErrorCodeDescriptor {
		UNVERIFIED,
		UNVERIFIED_CVC, // CVC: CARD VERIFICATION CODE
		UNVERIFIED_CVT, // CVT: CARD VALID THRU
	}

	public enum Business implements ErrorCodeDescriptor {
		BUSINESS,
	}
}
