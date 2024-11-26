package dogshop.market.config;

import dogshop.market.utility.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthenticationService {
    private final JwtDecoder jwtDecoder;

    public AuthenticationService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public String getUsername() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String authorizationHeader = request.getHeader("Authorization");

            System.out.println("Authorization Header: " + authorizationHeader);

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                System.err.println("Authorization header is missing or invalid");
                return "guest";
            }

            String token = authorizationHeader.split(" ")[1];
            System.out.println("Extracted Token: " + token);

            String username = JwtUtils.getNameFromToken(token); // Metodo per estrarre il nome utente
            System.out.println("Decoded Username: " + username);

            return username;
        } catch (Exception e) {
            e.printStackTrace();
            return "guest";
        }
    }


    public String getUserId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Authorization header is missing or invalid.");
            }

            String token = authorizationHeader.split(" ")[1];
            return JwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dell'ID utente: " + e.getMessage());
        }
    }

}
