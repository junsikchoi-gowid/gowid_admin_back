package com.nomadconnection.dapp.core.domain;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Benefit extends BaseTime {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Lob
	private String content;

	@Column(nullable = false)
	@Lob
	private String summary;

	private String orgfname; // 파일명(원본)

	private String fname; // 파일명

	private Long size;     // 파일사이즈

	private String s3Link; // s3주소

	private String s3Key; // s3key

	@Builder.Default
	private Boolean disabled = false;
}
