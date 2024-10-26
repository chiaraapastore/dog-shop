package com.example.dogshop.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dogshop.entity.Utente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Value("${keycloak.admin.access.secret}")
    private String secret;

    public static String getNameFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("preferred_username").asString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("sub").asString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Metodo per generare il token JWT
    public String generateToken(Utente utente) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(utente.getUsername() != null ? utente.getUsername() : "Unknown")
                .withClaim("username", utente.getUsername() != null ? utente.getUsername() : "Unknown")
                .withClaim("user_id", utente.getId() != null ? utente.getId().toString() : "0")
                .sign(algorithm);
    }
}
