package com.nomadconnection.dapp.core.domain.etc;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.SurveyType;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idx")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"idxUser", "title", "answer"}, name = "UK_idxUser_title_answer"))
public class Survey extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(columnDefinition = "varchar(30)  DEFAULT '' COMMENT   '설문조사 주제'")
	@Enumerated(value = EnumType.STRING)
	private CommonCodeType title;

	@Column(columnDefinition = "varchar(50)  DEFAULT '' COMMENT   '설문조사 답변'")
	@Enumerated(value = EnumType.STRING)
	private SurveyType answer;

	@Column(columnDefinition = "varchar(100)  DEFAULT '' COMMENT   '설문조사 답변 상세내용'")
	private String detail;

	@ManyToOne
	@JoinColumn(name = "idxUser", foreignKey = @ForeignKey(name = "FK_User_Survey"), columnDefinition = "bigint(20) COMMENT '사용자 idx'")
	private User user;

	public Optional getDetail() {
		return Optional.ofNullable(detail);
	}

}
