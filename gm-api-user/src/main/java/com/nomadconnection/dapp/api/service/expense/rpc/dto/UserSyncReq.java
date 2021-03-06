package com.nomadconnection.dapp.api.service.expense.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSyncReq {

    private String email;
    private String password;

    @Override
    public String toString() {
        return String.format("%s(email=%s, password=********)", getClass().getSimpleName(), email);
    }
}
