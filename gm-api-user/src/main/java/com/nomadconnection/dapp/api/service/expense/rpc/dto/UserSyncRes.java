package com.nomadconnection.dapp.api.service.expense.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSyncRes {
    private String contractorEmail;
    private String memberEmail;
    private String memberRoleType;
    private String name;
    private Boolean isInvitedUser;
}
