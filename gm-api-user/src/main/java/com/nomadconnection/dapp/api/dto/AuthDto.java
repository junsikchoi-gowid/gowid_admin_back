package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.CorpStatus;
import com.nomadconnection.dapp.core.domain.MemberAuthority;
import com.nomadconnection.dapp.core.domain.Role;
import com.nomadconnection.dapp.core.domain.embed.Address;
import com.nomadconnection.dapp.jwt.dto.TokenDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class AuthDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AuthInfo {

		@ApiModelProperty("식별자(사용자)")
		private Long idx;

		@ApiModelProperty("식별자(소속법인)")
		private Long idxCorp;

		@ApiModelProperty("식별자(카드)")
		private Long idxCard;

		@ApiModelProperty("이메일(계정)")
		private String email;

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("연락처(폰)")
		private String mdn;

		@ApiModelProperty("수령지(주소)")
		private Address recipientAddress;

		@ApiModelProperty("법인 등록 상태")
		private CorpStatus corpStatus;

		@ApiModelProperty("부가정보")
		private TokenDto.TokenSet.AccountInfo info;
	}
}
