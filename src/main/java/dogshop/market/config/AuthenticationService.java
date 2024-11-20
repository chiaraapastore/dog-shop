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

            return JwtUtils.getNameFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return "guest";
        }
    }
}
