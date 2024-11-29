package dogshop.market.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtUtils {

    private static String secret;

    public JwtUtils(@Value("${keycloak.admin.access.secret}") String secret) {
        this.secret = secret;
    }

    public static String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
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

}
