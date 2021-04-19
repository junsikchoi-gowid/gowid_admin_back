package com.nomadconnection.dapp.core.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Data
@Accessors(fluent = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Enumerated(EnumType.STRING)
	@Column(length = 32)
	private Role role;

	public static Role from(Set<Authority> authorities) {
		String GOWID_ADMIN_PREFIX = "GOWID_";
		Role current = Role.ROLE_MEMBER;

		for(Authority authority: authorities) {
			if(authority.role().name().startsWith(GOWID_ADMIN_PREFIX)) {
				continue;
			}
			if(authority.role().compareTo(current) < 0) {
				current = authority.role();
			}
		}

		return current;
	}
}
