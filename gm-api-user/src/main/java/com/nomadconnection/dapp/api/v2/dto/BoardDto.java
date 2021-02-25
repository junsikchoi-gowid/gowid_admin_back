package com.nomadconnection.dapp.api.v2.dto;

import lombok.*;

import java.time.LocalDateTime;

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

