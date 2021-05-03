package com.nomadconnection.dapp.api.service.expense.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialReq {
    String newEmail;
    String password;

    @Override
    public String toString() {
        return String.format("%s(newMail=%s, password=********)", getClass().getSimpleName(), newEmail);    }
}
