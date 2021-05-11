package com.nomadconnection.dapp.api.service.expense.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileReq {
    String email;
    String userName;
    String mobile;

    @Override
    public String toString() {
        return String.format("%s(newMail=%s, userName=%s, mobile=%s)", getClass().getSimpleName(), email, userName, mobile);
    }
}
