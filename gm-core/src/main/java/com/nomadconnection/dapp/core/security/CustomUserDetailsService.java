package com.nomadconnection.dapp.core.security;

import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByAuthentication_EnabledAndEmail(true,username).map(com.nomadconnection.dapp.core.security.CustomUser::new).orElseThrow(
                () -> new UsernameNotFoundException(String.format("`%s` not found", username))
        );
    }
}
