package com.ecobank.api.configuration;

import com.ecobank.api.models.jwt.JwtTokenData;
import com.ecobank.api.services.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtService jwtService;

    public JwtAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var jwt = (JwtTokenData) authentication;

        jwt = jwtService.verifyToken(jwt.getToken());
        if (!jwt.isVerified()) {
            throw new BadCredentialsException("");
        }

        return jwt;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtTokenData.class.isAssignableFrom(authentication);
    }
}
