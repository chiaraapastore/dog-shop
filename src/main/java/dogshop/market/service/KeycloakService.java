package dogshop.market.service;

import dogshop.market.client.KeycloakClient;
import dogshop.market.client.RoleKeycloak;
import dogshop.market.entity.TokenRequest;
import dogshop.market.entity.UtenteShop;
import dogshop.market.entity.UtenteKeycloak;
import dogshop.market.repository.UtenteShopRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeycloakService {

    private final KeycloakClient keycloakClient;
    private final UtenteShopRepository utenteShopRepository;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;


    @Value("${keycloak.admin.client-id}")
    private String clientIdAdmin;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecretAdmin;

    public KeycloakService(KeycloakClient keycloakClient, UtenteShopRepository utenteShopRepository) {
        this.keycloakClient = keycloakClient;
        this.utenteShopRepository = utenteShopRepository;
    }

    public String login(String username, String password) {
        TokenRequest tokenRequest = new TokenRequest(username, password, clientIdAdmin, clientSecretAdmin, "password");
        ResponseEntity<Object> responseEntity = keycloakClient.getAccessToken(tokenRequest);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> mapResponse = objectMapper.convertValue(responseEntity.getBody(), Map.class);
            return mapResponse.get("access_token").toString();
        } else {
            throw new RuntimeException("Login fallito: " + responseEntity.getStatusCode());
        }
    }

    @Transactional
    public UtenteShop createUtenteInKeycloak(UtenteShop utenteShop) {
        validateUtenteShop(utenteShop);

        String adminToken = getAdminAccessToken();

        // Controlla se l'utente esiste già
        List<UserRepresentation> existingUsers = keycloakClient.getUsersByUsername("Bearer " + adminToken, utenteShop.getUsername());
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("L'utente esiste già in Keycloak.");
        }

        // Crea l'utente in Keycloak
        UtenteKeycloak utenteKeycloak = convertToUtenteKeycloak(utenteShop);
        ResponseEntity<Object> response = keycloakClient.createUsers("Bearer " + adminToken, utenteKeycloak);

        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Errore durante la creazione dell'utente in Keycloak.");
        }

        // Recupera l'ID da Keycloak
        String keycloakId = extractKeycloakIdFromResponse(response);
        utenteShop.setKeycloakId(keycloakId);

        // Salva nel database
        UtenteShop savedUtenteShop = utenteShopRepository.save(utenteShop);

        // Assegna il ruolo
        assignRolesToUser("Bearer " + adminToken, keycloakId, utenteShop.getRole());

        return savedUtenteShop;
    }

    private void assignRolesToUser(String authorizationHeader, String userId, String role) {
        ResponseEntity<List<RoleKeycloak>> rolesResponse = keycloakClient.getAvailableRoles(authorizationHeader, userId, "0", "100");
        List<RoleKeycloak> roleList = rolesResponse.getBody();

        RoleKeycloak selectedRole = roleList.stream()
                .filter(r -> r.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato: " + role));

        List<RoleRepresentation> rolesToAssign = List.of(selectedRole.toRoleRepresentation());
        keycloakClient.addRoleToUser(authorizationHeader, userId, selectedRole.getClientId(), rolesToAssign);
    }

    public String getAdminAccessToken() {
        try {
            TokenRequest tokenRequest = new TokenRequest(adminUsername, adminPassword, clientIdAdmin, clientSecretAdmin, "password");
            ResponseEntity<Object> response = keycloakClient.getAccessToken(tokenRequest);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.convertValue(response.getBody(), Map.class);
                return (String) responseBody.get("access_token");
            } else {
                throw new RuntimeException("Impossibile ottenere il token di amministrazione");
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'autenticazione dell'amministratore: " + e.getMessage(), e);
        }
    }

    public String extractKeycloakIdFromResponse(ResponseEntity<Object> response) {
        if (response.getHeaders().containsKey("Location")) {
            String location = response.getHeaders().get("Location").get(0);
            return location.substring(location.lastIndexOf("/") + 1);
        }
        throw new RuntimeException("Impossibile estrarre l'ID utente dalla risposta di Keycloak");
    }

    private UtenteKeycloak convertToUtenteKeycloak(UtenteShop utenteShop) {
        UtenteKeycloak keycloak = new UtenteKeycloak();
        keycloak.setUsername(utenteShop.getUsername());
        keycloak.setFirstName(utenteShop.getFirstName());
        keycloak.setLastName(utenteShop.getLastName());
        keycloak.setEmail(utenteShop.getEmail());
        keycloak.setEnabled(true);
        return keycloak;
    }

    private void validateUtenteShop(UtenteShop utenteShop) {
        if (utenteShop.getFirstName() == null || utenteShop.getLastName() == null || utenteShop.getUsername() == null) {
            throw new IllegalArgumentException("I campi obbligatori non sono valorizzati.");
        }
    }
}
