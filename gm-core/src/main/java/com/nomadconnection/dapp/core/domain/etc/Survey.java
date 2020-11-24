package com.nomadconnection.dapp.core.domain.etc;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idx", callSuper = false)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title", "answer"}, name = "UK_title"))
public class Survey extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(columnDefinition = "varchar(30) NOT NULL DEFAULT '' COMMENT   '설문조사 주제'")
	private String title;

	@Column(columnDefinition = "varchar(30) NOT NULL DEFAULT '' COMMENT   '설문조사 주제 내용'")
	private String titleName;

	@Column(columnDefinition = "varchar(50) NOT NULL DEFAULT '' COMMENT   '설문조사 답변'")
	private String answer;

	@Column(columnDefinition = "varchar(50) NOT NULL DEFAULT '' COMMENT   '설문조사 답변 내용'")
	private String answerName;

	@Column(columnDefinition = "varchar(20) NOT NULL DEFAULT '' COMMENT   '설문조사 답변 종류'")
	private String answerType;

	@Column(columnDefinition = "smallint DEFAULT 0 COMMENT '설문조사 답변 순서'")
	private Integer answerOrder;

	@Column(columnDefinition = "varchar(100) DEFAULT NULL COMMENT   '설문조사 선택항목'")
	private String items;

	@Column(columnDefinition = "tinyint(1)  DEFAULT false COMMENT '설문조사 활성화여부'")
	private Boolean activated;


}
