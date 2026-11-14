
package com.previ.previ_backend.security;

import com.previ.previ_backend.repository.UsuarioRepository;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

@Configuration
public class JwtConfig {

    public static final String ISSUER = "previ-backend";

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret-base64}") String base64) {

        byte[] bytes = Base64.getDecoder().decode(base64);

        if (bytes.length != 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET_BASE64 precisa conter exatamente 32 bytes"
            );
        }

        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {

        JWK jwk = new OctetSequenceKey.Builder(jwtSecretKey)
                .algorithm(JWSAlgorithm.HS256)
                .build();

        JWKSource<SecurityContext> jwkSource =
                (selector, context) ->
                        selector.select(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey jwtSecretKey,
            UsuarioRepository usuarioRepository) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();

        OAuth2TokenValidator<Jwt> usuarioAtivo = jwt -> {

            UUID usuarioId;

            try {
                usuarioId = UUID.fromString(jwt.getSubject());
            } catch (IllegalArgumentException |
                     NullPointerException ex) {

                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error(
                                "invalid_token",
                                "Identificador de usuário inválido",
                                null
                        )
                );
            }

            boolean ativo = usuarioRepository
                    .findByIdAndDeletedAtIsNull(usuarioId)
                    .isPresent();

            if (!ativo) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error(
                                "invalid_token",
                                "Usuário inexistente ou desativado",
                                null
                        )
                );
            }

            return OAuth2TokenValidatorResult.success();
        };

        OAuth2TokenValidator<Jwt> validadores =
                new DelegatingOAuth2TokenValidator<>(
                        JwtValidators.createDefaultWithIssuer(ISSUER),
                        usuarioAtivo
                );

        decoder.setJwtValidator(validadores);

        return decoder;
    }
}
