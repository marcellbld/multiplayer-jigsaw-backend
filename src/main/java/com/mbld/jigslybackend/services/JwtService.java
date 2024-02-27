package com.mbld.jigslybackend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.expiration-time}")
    private long expirationTime;
    @Value("${security.jwt.token-prefix}")
    private String tokenPrefix;
    @Value("${security.jwt.header-string}")
    private String headerString;

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("sub").asString());
    }

    public <T> T extractClaim(String token, Function<Map<String, Claim>, T> claimsResolver) {
        Map<String, Claim> claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, Collections.emptyMap());
    }

    public String generateToken(
            UserDetails userDetails,
            Map<String, String> extraClaims
    ) {
        System.out.println(Arrays.toString(userDetails.getAuthorities().toArray()));
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("role", userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority());
        extraClaims.forEach(jwtBuilder::withClaim);

        return jwtBuilder.sign(Algorithm.HMAC256(secret));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, claims -> claims.get("exp").asDate());
    }

    private Map<String, Claim> extractAllClaims(String token) {
        return decodeJWT(token).getClaims();
    }

    private DecodedJWT decodeJWT(String token) throws TokenExpiredException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        return verifier.verify(token);
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String getHeaderString() {
        return headerString;
    }
}
