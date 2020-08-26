package com.nomadconnection.dapp.api.dto.lotte;

import com.nomadconnection.dapp.api.dto.lotte.enums.Status;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusDto {

	@Setter
	private Status status;

}
