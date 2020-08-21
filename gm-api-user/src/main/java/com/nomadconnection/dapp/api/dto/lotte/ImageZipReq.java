package com.nomadconnection.dapp.api.dto.lotte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageZipReq {
	private String licenseNo;
	private String registrationNo;
	private String enrollmentDate;
}
