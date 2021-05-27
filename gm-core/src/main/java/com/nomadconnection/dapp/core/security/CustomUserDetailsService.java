package com.nomadconnection.dapp.core.security;

import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        CustomUser user = repo.findByAuthentication_EnabledAndEmail(true, username).map(CustomUser::new).orElseThrow(
                () -> new UsernameNotFoundException(String.format("`%s` not found", username)));

        Hibernate.initialize(user.corp());

        return user;
    }
}
