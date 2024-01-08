package com.ecobank.api.models.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtTokenData implements Authentication {
    private String token;
    private boolean isVerified;
    private String userEmail;
    private Date issuedAt;
    private Date expiration;

    public JwtTokenData(String token) {
        this.token = token;
        isVerified = false;
        userEmail = null;
        issuedAt = null;
        expiration = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return null;
    }

    @Override
    public Object getCredentials() {

        return token;
    }

    @Override
    public Object getDetails() {

        return null;
    }

    @Override
    public Object getPrincipal() {

        return userEmail;
    }

    @Override
    public boolean isAuthenticated() {

        return isVerified;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        isVerified = isAuthenticated;
    }

    @Override
    public String getName() {

        return userEmail;
    }
}
