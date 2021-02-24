package com.nomadconnection.dapp.api.v2.dto;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.limit.ContactType;
import com.nomadconnection.dapp.core.domain.limit.GrantStatus;
import com.nomadconnection.dapp.core.domain.limit.LimitRecalculation;
import com.nomadconnection.dapp.core.domain.limit.ReviewStatus;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
public class BoardDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Notice {
		private Long idx;
		private String irType;
		private String title;
		private String contents;
		private Long idxUser;
		private Boolean enable;
		private Boolean replay;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
	}

}

