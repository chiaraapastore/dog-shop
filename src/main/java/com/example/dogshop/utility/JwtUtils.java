package com.example.dogshop.utility;

import com.example.dogshop.entity.Utente;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;



public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;
    private Utente utente;

    public static String getNameFromToken(String token) {
        try{
            DecodedJWT decodedJWT = JWT.decode(token);
            String username = decodedJWT.getClaim("preferred_username").asString();
            return username;
        }catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIdFromToken(String token) {
        try{
            DecodedJWT decodedJWT = JWT.decode(token);
            String id = decodedJWT.getClaim("sub").asString();
            return id;
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String generateToken(Utente utente) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(utente.getFirstName()) // Usa il Name come 'sub'
                .withClaim("preferred_username", utente.getEmail())
                .sign(algorithm);
    }


}
