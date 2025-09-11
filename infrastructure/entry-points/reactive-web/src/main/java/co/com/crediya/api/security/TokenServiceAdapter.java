package co.com.crediya.api.security;

import java.util.UUID;

import javax.crypto.SecretKey;

import co.com.crediya.model.loanaplication.gateways.TokenServiceGateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;


@Component
public class TokenServiceAdapter implements TokenServiceGateway {
    
    private static final Logger log = LoggerFactory.getLogger(TokenServiceAdapter.class);

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<UUID> getClientId() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(auth -> auth instanceof JwtAuthenticationToken) 
            .map(auth -> (JwtAuthenticationToken) auth)
            .map(jwt -> UUID.fromString(jwt.getToken().getSubject())); 
    }

    
    /*@Override
    public Mono<UUID> getClientId() {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication().getCredentials().toString()) // aquí asumo que guardaste el token como credentials
            .doOnNext(token -> log.info("Token recuperado del contexto: {}", token))
            .map(this::extractClientId);
    }*/

    private UUID extractClientId(String token){
        return UUID.fromString(getClaims(token).getSubject());
    }

    private Claims getClaims(String token) {  
        return Jwts.parser()  
                .verifyWith(getKey(secret))  
                .build()  
                .parseSignedClaims(token)  
                .getPayload();  
    }

    private SecretKey getKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    } 
}
