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
                .withSubject(utente.getFirstName() != null ? utente.getFirstName() : "Unknown") // Usa il First Name come 'sub'
                .withClaim("preferred_username", utente.getEmail() != null ? utente.getEmail() : "unknown@example.com")
                .withClaim("user_id", utente.getId() != null ? utente.getId().toString() : "0") // Aggiunge l'ID utente come claim
                .sign(algorithm);
    }
}
