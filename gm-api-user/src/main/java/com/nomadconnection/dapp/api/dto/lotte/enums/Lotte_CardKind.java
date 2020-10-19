package com.nomadconnection.dapp.api.dto.lotte.enums;

import com.nomadconnection.dapp.core.domain.lotte.Lotte_D1100;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Lotte_CardKind {
	GREEN("01", "C313333", "02", "4", "3"),
	BLACK("01", "C313334", "02", "4", "3"),
	GREEN_TRAFFIC("01", "C313438", "02", "4", "3"),
	BLACK_TRAFFIC("01", "C313437", "02", "4", "3"),
	HI_PASS("01", "C313469", "02", "4", "1"),
	;

	private String productCode;
	private String cardKindCode;
	private String logoCode;
	private String cardTypeCode;
	private String gradeCode;

	public static Lotte_D1100 setCardKindInLotte_D1100(Lotte_D1100 d1100, Lotte_CardKind cardKind, String count, int seq) {
		switch (seq) {
			case 1:
				return d1100.setCocPdDc(cardKind.getProductCode())
						.setUnitCdC(cardKind.getCardKindCode())
						.setBraDc(cardKind.getLogoCode())
						.setCdDc(cardKind.getCardTypeCode())
						.setCdGdc(cardKind.getGradeCode())
						.setRgAkCt(count);

			case 2:
				return d1100.setCocPdDc2(cardKind.getProductCode())
						.setUnitCdC2(cardKind.getCardKindCode())
						.setBraDc2(cardKind.getLogoCode())
						.setCdDc2(cardKind.getCardTypeCode())
						.setCdGdc2(cardKind.getGradeCode())
						.setRgAkCt2(count);

			case 3:
				return d1100.setCocPdDc3(cardKind.getProductCode())
						.setUnitCdC3(cardKind.getCardKindCode())
						.setBraDc3(cardKind.getLogoCode())
						.setCdDc3(cardKind.getCardTypeCode())
						.setCdGdc3(cardKind.getGradeCode())
						.setRgAkCt3(count);

			case 4:
				return d1100.setCocPdDc4(cardKind.getProductCode())
						.setUnitCdC4(cardKind.getCardKindCode())
						.setBraDc4(cardKind.getLogoCode())
						.setCdDc4(cardKind.getCardTypeCode())
						.setCdGdc4(cardKind.getGradeCode())
						.setRgAkCt4(count);

			case 5:
				return d1100.setCocPdDc5(cardKind.getProductCode())
						.setUnitCdC5(cardKind.getCardKindCode())
						.setBraDc5(cardKind.getLogoCode())
						.setCdDc5(cardKind.getCardTypeCode())
						.setCdGdc5(cardKind.getGradeCode())
						.setRgAkCt5(count);

			default:
				return d1100;
		}
	}

	public static Lotte_D1100 initCardKindInLotte_D1100(Lotte_D1100 d1100) {
		return d1100.setCocPdDc("")
				.setUnitCdC("")
				.setBraDc("")
				.setCdDc("")
				.setCdGdc("")
				.setRgAkCt("")
				.setCocPdDc2("")
				.setUnitCdC2("")
				.setBraDc2("")
				.setCdDc2("")
				.setCdGdc2("")
				.setRgAkCt2("")
				.setCocPdDc3("")
				.setUnitCdC3("")
				.setBraDc3("")
				.setCdDc3("")
				.setCdGdc3("")
				.setRgAkCt3("")
				.setCocPdDc4("")
				.setUnitCdC4("")
				.setBraDc4("")
				.setCdDc4("")
				.setCdGdc4("")
				.setRgAkCt4("")
				.setCocPdDc5("")
				.setUnitCdC5("")
				.setBraDc5("")
				.setCdDc5("")
				.setCdGdc5("")
				.setRgAkCt5("");
	}
}
