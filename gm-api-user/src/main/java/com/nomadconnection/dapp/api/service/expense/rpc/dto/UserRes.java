package com.nomadconnection.dapp.api.service.expense.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRes {
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String activatedAt;
    private String deactivatedAt;
    private String department;
    private String email;
    private String imgUrl;
    private Boolean isActivated;
    private Boolean isAdmin;
    private Boolean isInvitedUser;
    private String mobileNumber;
    private Boolean notificationOnOff;
    private String position;
    private String userName;
    private Long corpId;
    private String osType;
}
