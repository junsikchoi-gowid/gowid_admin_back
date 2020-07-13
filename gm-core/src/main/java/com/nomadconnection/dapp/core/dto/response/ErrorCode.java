package com.nomadconnection.dapp.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
		EXCESS_RESOURCE,
		ENTITY_NOT_FOUND,
	}

	public enum Mismatched implements ErrorCodeDescriptor {
		MISMATCHED,
		MISMATCHED_PASSWORD,
		MISMATCHED_JWT_SUBJECT,
		MISMATCHED_VERIFICATION_CODE,
		MISMATCHED_VALID_THRU,
		MISMATCHED_CORP,
		MISMATCHED_CARD_ISSUANCE_INFO,
		MISMATCHED_STOCKHOLDER_FILE,
		MISMATCHED_CEO
	}

	public enum Unverified implements ErrorCodeDescriptor {
		UNVERIFIED,
		UNVERIFIED_CVC, // CVC: CARD VERIFICATION CODE
		UNVERIFIED_CVT, // CVT: CARD VALID THRU
	}

	public enum Business implements ErrorCodeDescriptor {
		BUSINESS,
	}

	@AllArgsConstructor
	@Getter
	public enum External {
		EXTERNAL_ERROR_GW("EXTERNAL_ERROR_GW", "external error(shinhan - GW)"),
		INTERNAL_ERROR_GW("INTERNAL_ERROR_GW", "internal error(shinhan - GW)"),

		INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "internal server error"),

		EXTERNAL_ERROR_SHINHAN_1200("EXTERNAL_ERROR_SHINHAN_1200", "external error(shinhan - 1200)"),
		REJECTED_SHINHAN_1200("EXTERNAL_ERROR_SHINHAN_1200", "rejected(shinhan - 1200)"),
		INTERNAL_ERROR_SHINHAN_1200("INTERNAL_ERROR_SHINHAN_1200", "internal error(shinhan - 1200)"),

		EXTERNAL_ERROR_SHINHAN_3000("EXTERNAL_ERROR_SHINHAN_3000", "external error(shinhan - 3000)"),
		REJECTED_SHINHAN_3000("EXTERNAL_ERROR_SHINHAN_3000", "rejected(shinhan - 3000)"),
		INTERNAL_ERROR_SHINHAN_3000("INTERNAL_ERROR_SHINHAN_3000", "internal error(shinhan - 3000)"),

		EXTERNAL_ERROR_SHINHAN_BPR_TRANSFER("EXTERNAL_ERROR_SHINHAN_BPR_TRANSFER", "external error(shinhan - BPR_TRANSFER)"),
		REJECTED_SHINHAN_BPR_TRANSFER("REJECTED_SHINHAN_BPR_TRANSFER", "rejected(shinhan - BPR_TRANSFER)"),
		INTERNAL_ERROR_SHINHAN_BPR_TRANSFER("INTERNAL_ERROR_SHINHAN_BPR_TRANSFER", "internal error(shinhan - BPR_TRANSFER)"),

		EXTERNAL_ERROR_SHINHAN_1510("EXTERNAL_ERROR_SHINHAN_1510", "external error(shinhan - 1510)"),
		REJECTED_SHINHAN_1510("REJECTED_SHINHAN_1510", "rejected(shinhan - 1510) "),
		INTERNAL_ERROR_SHINHAN_1510("INTERNAL_ERROR_SHINHAN_1510", "internal error(shinhan - 1510)"),

		EXTERNAL_ERROR_SHINHAN_1520("EXTERNAL_ERROR_SHINHAN_1520", "external error(shinhan - 1520)"),
		REJECTED_SHINHAN_1520("REJECTED_SHINHAN_1520", "rejected(shinhan - 1520) "),
		INTERNAL_ERROR_SHINHAN_1520("INTERNAL_ERROR_SHINHAN_1520", "internal error(shinhan - 1520)"),

		EXTERNAL_ERROR_SHINHAN_1530("EXTERNAL_ERROR_SHINHAN_1530", "external error(shinhan - 1530)"),
		REJECTED_SHINHAN_1530("EXTERNAL_ERROR_SHINHAN_1530", "rejected(shinhan - 1530) "),
		INTERNAL_ERROR_SHINHAN_1530("INTERNAL_ERROR_SHINHAN_1530", "internal error(shinhan - 1530)"),

		EXTERNAL_ERROR_SHINHAN_1400("EXTERNAL_ERROR_SHINHAN_1400", "external error(shinhan - 1400)"),
		REJECTED_SHINHAN_1400("REJECTED_SHINHAN_1400", "rejected(shinhan - 1400) "),
		HOLD_SHINHAN_1400("HOLDING_SHINHAN_1400", "holding(shinhan - 1400)"),
		INTERNAL_ERROR_SHINHAN_1400("INTERNAL_ERROR_SHINHAN_1400", "internal error(shinhan - 1400)"),

		EXTERNAL_ERROR_SHINHAN_1000("EXTERNAL_ERROR_SHINHAN_1000", "external error(shinhan - 1000)"),
		REJECTED_SHINHAN_1000("EXTERNAL_ERROR_SHINHAN_1000", "rejected(shinhan - 1000) "),
		HOLD_SHINHAN_1000("EXTERNAL_ERROR_SHINHAN_1000", "holding(shinhan - 1000)"),
		INTERNAL_ERROR_SHINHAN_1000("INTERNAL_ERROR_SHINHAN_1000", "internal error(shinhan - 1000)"),

		EXTERNAL_ERROR_SHINHAN_1100("EXTERNAL_ERROR_SHINHAN_1100", "external error(shinhan - 1100)"),
		REJECTED_SHINHAN_1100("EXTERNAL_ERROR_SHINHAN_1100", "rejected(shinhan - 1100) "),
		INTERNAL_ERROR_SHINHAN_1100("INTERNAL_ERROR_SHINHAN_1100", "internal error(shinhan - 1100)"),

		EXTERNAL_ERROR_SHINHAN_1700("EXTERNAL_ERROR_SHINHAN_1700", "external error(shinhan - 1700)"),
		REJECTED_SHINHAN_1700("EXTERNAL_ERROR_SHINHAN_1700", "rejected(shinhan - 1700) "),
		INTERNAL_ERROR_SHINHAN_1700("INTERNAL_ERROR_SHINHAN_1700", "internal error(shinhan - 1700)"),

		EXTERNAL_ERROR_SHINHAN_1800("EXTERNAL_ERROR_SHINHAN_1800", "external error(shinhan - 1800)"),
		REJECTED_SHINHAN_1800("EXTERNAL_ERROR_SHINHAN_1800", "rejected(shinhan - 1800) "),
		INTERNAL_ERROR_SHINHAN_1800("INTERNAL_ERROR_SHINHAN_1800", "internal error(shinhan - 1800)");

		private final String code;
		private final String desc;
	}

	@AllArgsConstructor
	@Getter
	public enum Api {
		VALIDATION_FAILED("VALIDATION_FAILED", "validation failed"),
		NOT_FOUND("NOT_FOUND", "not found");

		private final String code;
		private final String desc;
	}
}
