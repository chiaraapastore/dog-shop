package com.example.dogshop.config;


import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
//Converte i token JWT in una collezione di GrantedAuthority, che rappresentano le autorizzazioni dell'utente nel sistema.
public class KeycloakJwtTokenConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    //Estrazione dal token
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "role_prefix";
    // Istanziazione diretta del converter
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
    private final TokenConverterPropierties tokenConverterPropierties;

    public KeycloakJwtTokenConverter(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter, TokenConverterPropierties tokenConverterPropierties) {
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
        this.tokenConverterPropierties = tokenConverterPropierties;
    }

    //Conversione
    //Converte un token JWT in una lista di GrantedAuthority. Estrae i ruoli dai claim resource_access e li converte in oggetti GrantedAuthority.
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        LinkedList result = new LinkedList<>();
        try {
            try {
                Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);
                Map<String, Object> clientIdMap = (Map<String, Object>) resourceAccess.get(tokenConverterPropierties.getResourceId());
                List<String> roles = (List<String>) clientIdMap.get(ROLES);
                Collection<GrantedAuthority> resourceRoles = roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role)).collect(Collectors.toList());
                result.addAll(resourceRoles);
            }catch(Exception e){
                e.printStackTrace();
            }
            try {
                Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
                List<String> realmRoles = (List<String>) realmAccess.get(ROLES);
                Collection<GrantedAuthority> realmAuthorities = realmRoles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role)).collect(Collectors.toList());
                result.addAll(realmAuthorities);
            }catch(Exception e){
                e.printStackTrace();
            }
            return result;
        }catch(Exception e){
            e.printStackTrace();
            return result;
        }
    }

}

