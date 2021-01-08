package com.nomadconnection.dapp.api.service.expense;

import com.nomadconnection.dapp.api.service.expense.rpc.ExpenseRpc;
import com.nomadconnection.dapp.api.service.expense.rpc.dto.UserRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRpc expenseRpc;

    public boolean isAppUser(String email) {
        UserRes expenseUser = expenseRpc.requestUser(email);
        if (expenseUser == null) {
            return false;
        }

        return !expenseUser.getIsAdmin();
    }

}
