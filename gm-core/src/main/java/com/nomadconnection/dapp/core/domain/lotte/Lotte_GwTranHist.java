package com.nomadconnection.dapp.core.domain.lotte;

import com.nomadconnection.dapp.core.domain.audit.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lotte_GwTranHist extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long idx;

	@Column(columnDefinition = "varchar(4) COMMENT '전문종별코드'")
	private String protocolCode;  // 전문종별코드

	@Column(columnDefinition = "varchar(1) COMMENT '송수신구분코드'")
	private String transferCode;  // 송수신 flag

	@Column(columnDefinition = "varchar(22) COMMENT '거래고유번호(Globally Unique Identifier)'")
	private String guid;  // 거래고유번호(Globally Unique Identifier)

	@Column(columnDefinition = "varchar(14) COMMENT '전문전송일시'")
	private String transferDate;  // 전문전송일시

	@Column(columnDefinition = "varchar(4) COMMENT '응답코드'")
	private String responseCode;  // 응답코드

	@Column(columnDefinition = "varchar(100) COMMENT '응답메시지'")
	private String spare;  // 응답메시지

	@Column
	private Long userIdx;

	@Column
	private Long corpIdx;
}
