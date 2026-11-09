package com.previ.previ_backend.security;

import com.previ.previ_backend.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final long accessTokenMinutes;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${app.jwt.access-token-minutes:15}") long accessTokenMinutes) {
        this.jwtEncoder = jwtEncoder;
        if (accessTokenMinutes <= 0) {
            throw new IllegalArgumentException("A validade do JWT deve ser positiva");
        }
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public String gerarAccessToken(Usuario usuario,
                                   Collection<? extends GrantedAuthority> authorities) {
        Instant agora = Instant.now();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(JwtConfig.ISSUER)
                .issuedAt(agora)
                .expiresAt(agora.plus(accessTokenMinutes, ChronoUnit.MINUTES))
                .subject(usuario.getId().toString())
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpiresInSeconds() {
        return accessTokenMinutes * 60;
    }
}
