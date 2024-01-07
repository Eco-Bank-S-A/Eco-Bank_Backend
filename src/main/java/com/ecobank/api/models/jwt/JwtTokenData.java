package com.ecobank.api.models.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtTokenData {
    private boolean isVerified;
    private String userEmail;
    private Date issuedAt;
    private Date expiration;
}
