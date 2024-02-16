package com.myproject.luharcom.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.luharcom.models.LoginDto;
import com.myproject.luharcom.models.User;
import com.myproject.luharcom.models.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
public class JwtHelper {


    @Autowired
    AuthenticationManager authenticationManager;

    @Value("${jwt.secret}")
    private String secret;

    public PrincipalUser getUserFromToken(String token){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        PrincipalUser userPrincipal = new PrincipalUser();
        String decodedPayload = decodeBase64((decodedJWT.getPayload()));
        userPrincipal.setUsername(extractUsernameFromJson(decodedPayload));
        userPrincipal.setEmail(decodedJWT.getClaim("e").asString());
        return userPrincipal;
    }

    private String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    private String extractUsernameFromJson(String decodedSubject) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(decodedSubject);
            return jsonNode.get("sub").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromClaims(DecodedJWT decodedJWT){
        var claims = decodedJWT.getClaim("a");
        if(claims.isNull() || claims.isMissing()) return List.of();
        return claims.asList(SimpleGrantedAuthority.class);
    }

    public String doGenerateToken(String subject, String email) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(Instant.now().plus(Duration.ofMinutes(15)))
                .withClaim("e",email)
                .sign(Algorithm.HMAC256(secret));
    }

    public LoginDto issueToken(User user){
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        var principal =(PrincipalUser) auth.getPrincipal();
        LoginDto loginDto = new LoginDto();
        loginDto.setTtl(Instant.now().plus(Duration.ofMinutes(15)));
        loginDto.setToken(doGenerateToken(principal.getUsername(), principal.getEmail()));
        return loginDto;
    }

}