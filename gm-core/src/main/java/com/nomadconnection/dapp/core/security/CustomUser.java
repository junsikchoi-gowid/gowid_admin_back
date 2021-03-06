package com.nomadconnection.dapp.core.security;

import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class CustomUser extends org.springframework.security.core.userdetails.User {

    private Long idx;
    private String email;
    private String name;
    private String uriProfileImage;
    private User user;
    private Corp corp;

    public CustomUser(User user) {
        super(
                user.email(), "",
                user.authentication().isEnabled(),
                user.authentication().isAccountNonExpired(),
                user.authentication().isCredentialsNonExpired(),
                user.authentication().isAccountNonLocked(),
                user.authorities()
                        .stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.role().name()))
                        .collect(Collectors.toList())
        );
        idx = user.idx();
        name = user.name();
        email = user.email();

        corp = user.corp();
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format("CustomUser(idx=%d, username=%s, authorities=%s)", idx, getUsername(), getAuthorities());
    }
}
