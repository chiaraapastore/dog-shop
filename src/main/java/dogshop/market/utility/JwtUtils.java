package dogshop.market.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dogshop.market.entity.UtenteShop;
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

    public String generateToken(UtenteShop utenteShop) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(utenteShop.getUsername() != null ? utenteShop.getUsername() : "Unknown")
                .withClaim("username", utenteShop.getUsername() != null ? utenteShop.getUsername() : "Unknown")
                .withClaim("user_id", utenteShop.getId() != null ? utenteShop.getId().toString() : "0")
                .sign(algorithm);
    }
}
