package com.nomadconnection.dapp.core.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessResponse {
    private String category;
    private String value;
    private String reason;
    private LocalDateTime current;
}
