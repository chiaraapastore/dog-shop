package dogshop.market.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KeycloakJwtTokenConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter;

    public KeycloakJwtTokenConverter(JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter) {
        this.grantedAuthoritiesConverter = grantedAuthoritiesConverter;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        LinkedList<GrantedAuthority> result = new LinkedList<>();

        try {
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            Map<String, Object> clientIdMap = (Map<String, Object>) resourceAccess.get("dog-shop-app");
            List<String> roles = (List<String>) clientIdMap.get("roles");

            if (roles != null) {
                Collection<GrantedAuthority> resourceRoles = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                result.addAll(resourceRoles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.addAll(grantedAuthoritiesConverter.convert(jwt));
        return result;
    }
}
