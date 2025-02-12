package com.localhost.anonymouschat.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



public enum Role {
    ADMIN(
            Set.of(Privilege.READ_PRIVILEGE, Privilege.WRITE_PRIVILEGE, Privilege.UPDATE_PRIVILEGE, Privilege.DELETE_PRIVILEGE)
    ),
    USER(
            Set.of(Privilege.READ_PRIVILEGE, Privilege.WRITE_PRIVILEGE)
    );

    private final Set<Privilege> privileges;

    // Enum constructor
    Role(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = privileges
                .stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}

