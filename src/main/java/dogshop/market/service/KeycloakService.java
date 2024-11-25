package dogshop.market.service;

import dogshop.market.client.KeycloakClient;
import dogshop.market.config.AuthenticationService;
import dogshop.market.entity.TokenRequest;
import dogshop.market.entity.UtenteShop;
import dogshop.market.repository.UtenteShopRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakClient keycloakClient;
    private final UtenteShopRepository utenteShopRepository;


    @Value("${keycloak.admin.client-id}")
    private String clientIdAdmin;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecretAdmin;


    public String login(String username, String password) {
        TokenRequest tokenRequest = new TokenRequest(username, password, clientIdAdmin, clientSecretAdmin, "password");
        ResponseEntity<Object> responseEntity = keycloakClient.getAccessToken(tokenRequest);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> mapResponse = objectMapper.convertValue(responseEntity.getBody(), Map.class);
            return mapResponse.get("access_token").toString();
        } else {
            throw new RuntimeException("Login failed with status: " + responseEntity.getStatusCode());
        }
    }


    @Transactional
    public ResponseEntity<UtenteShop> createUser(UtenteShop utenteShop) throws RuntimeException {
        Optional<UtenteShop> existingUtente = Optional.ofNullable(utenteShopRepository.findByUsername(utenteShop.getUsername()));
        if (existingUtente.isPresent()) {
            throw new RuntimeException("Utente con email già esistente nel database: " + utenteShop.getUsername());
        }

        String keycloakId = utenteShop.getKeycloakId();
        if (keycloakId == null || keycloakId.isEmpty()) {
            throw new RuntimeException("ID di Keycloak non valido o non presente.");
        }

        utenteShop.setKeycloakId(keycloakId);

        UtenteShop savedUtenteShop = utenteShopRepository.save(utenteShop);


        return ResponseEntity.ok(savedUtenteShop);
    }




}