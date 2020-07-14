package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface DeptCustomRepository {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class DeptWithMemberCountDto {

		@ApiModelProperty("식별자(부서)")
		private Long idx;

		@ApiModelProperty("멤버수")
		private Long members;

		@ApiModelProperty("부서명")
		private String name;
	}


	List<DeptWithMemberCountDto> findDeptWithMemberCount(Corp corp);
}
