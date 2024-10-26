package com.example.dogshop.config;

import com.example.dogshop.utility.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthenticationService { // La classe gestisce la decodifica dei token jwt per estrarre informazioni come username, id utente e ruoli di realm.
    private final JwtDecoder jwtDecoder; ////Autenticare l'utente

    public AuthenticationService(JwtDecoder jwtDecoder) { //Costruttore, responsabile della decodifica del token jwt
        this.jwtDecoder = jwtDecoder;
    }
    // Metodo per ottenere l'username dal token JWT, per estrapolare username-email
    // Correggi il recupero del token in `AuthenticationService`
    public String getUsername() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization").split(" ")[1];  // Corretta estrazione del token dopo "Bearer"
            return JwtUtils.getNameFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return "guest";
        }
    }



    // Metodo per ottenere l'id utente dal token JWT attraverso i claim
    // I claim sono informazioni che vengono contenute nel JWT, come dati dell'utente, autorizzazioni, etc.
    public String getUserId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization").split(" ")[1];
            return JwtUtils.getIdFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return "No_id";
        }
    }
}
