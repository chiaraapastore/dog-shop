package dogshop.market.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import dogshop.market.entity.UtenteShop;

@Component
public class JwtUtils {

    @Value("${keycloak.admin.access.secret}")
    private static String secret;

    // Metodo per ottenere l'ID utente dal token JWT usando JwtParser
    public static String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes()) // Assicuriamoci che la chiave sia in formato byte[]
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // Supponendo che il Subject contenga l'ID utente
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'estrazione dell'ID utente dal token.", e);
        }
    }

    public static String getNameFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("preferred_username").asString();
        } catch (Exception e) {
            System.err.println("Errore nella decodifica del token: " + e.getMessage());
            return "";
        }
    }

    public String generateToken(UtenteShop utenteShop) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withSubject(utenteShop.getId() != null ? utenteShop.getId().toString() : "0")
                    .withClaim("username", utenteShop.getUsername() != null ? utenteShop.getUsername() : "Unknown")
                    .withClaim("email", utenteShop.getEmail() != null ? utenteShop.getEmail() : "Unknown")
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la generazione del token.", e);
        }
    }
}
