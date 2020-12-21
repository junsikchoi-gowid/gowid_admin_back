package com.nomadconnection.dapp.core.domain.repository.external;


import com.nomadconnection.dapp.core.domain.external.ExternalCompanyType;
import com.nomadconnection.dapp.core.domain.external.UserExternalLink;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserExternalLinkRepo extends JpaRepository<UserExternalLink, Long> {

    Optional<UserExternalLink> findByUserAndExternalCompanyType(User user, ExternalCompanyType externalCompanyType);
}
