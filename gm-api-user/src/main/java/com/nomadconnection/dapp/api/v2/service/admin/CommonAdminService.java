package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.Role;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonAdminService {
    private final UserRepository userRepository;

    public boolean isGowidAdmin(long idxUser) {
        User user = userRepository.findById(idxUser).orElseThrow(
            () -> UserNotFoundException.builder().build()
        );
        return user.authorities().stream()
            .anyMatch(o -> o.role().equals(Role.GOWID_ADMIN));
    }
}
