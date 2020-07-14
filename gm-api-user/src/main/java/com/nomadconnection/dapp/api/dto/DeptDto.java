package com.nomadconnection.dapp.api.dto;

import com.nomadconnection.dapp.core.domain.corp.Dept;
import com.nomadconnection.dapp.core.domain.user.MemberAuthority;
import com.nomadconnection.dapp.core.domain.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class DeptDto {

	@ApiModelProperty("식별자")
	private Long idx;

	@ApiModelProperty("부서명")
	private String name;

	public DeptDto(Dept dept) {
		idx = dept.idx();
		name = dept.name();
	}

	@Data
	@Accessors(chain = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeptMember {

		@ApiModelProperty("식별자(사용자)")
		private Long idxUser;

		@ApiModelProperty("식별자(부서)")
		private Long idxDept;

		@ApiModelProperty("권한(멤버/어드민/마스터)")
		private MemberAuthority authority;

		@ApiModelProperty("이름")
		private String name;

		@ApiModelProperty("부서명")
		private String dept;

		@ApiModelProperty("경로(사진)")
		private String uriProfileImage;

		public static DeptMember from(User user) {
			DeptMember member = DeptMember.builder()
					.idxUser(user.idx())
					.authority(MemberAuthority.from(user.authorities()))
					.name(user.name())
					.build();
			if (user.dept() != null) {
				member.setIdxDept(user.dept().idx());
				member.setDept(user.dept().name());
			}
			if (user.profileResx() != null) {
				//
				//	todo: make or get profile image uri
				//
			}
			return member;
		}
	}
}
