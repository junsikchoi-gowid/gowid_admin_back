package com.nomadconnection.dapp.core.domain.repository.querydsl;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.res.ResAccountHistory;

import java.util.List;

public interface ConnectedMngCustomRepository {
    ResAccountHistory accountHistoryUpdateTime(Corp corp);

    ResAccount accountUpdateTime(Corp corp);

    List<ResAccount> accountList(Corp corp);
}
