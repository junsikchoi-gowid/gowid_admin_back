package com.nomadconnection.dapp.core.domain;


import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Columns;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class Faq extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Email
	private String email;

	@Column(nullable = false, length = 65535, columnDefinition = "Text")
	private String contents;

	@Column(nullable = false)
	private boolean replyStatus;
}
