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

    @Value("${keycloak.auth-server-url}")
    private String urlKeycloak;

    @Value("${keycloak.admin.client-id}")
    private String clientIdAdmin;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecretAdmin;

    public KeycloakService( KeycloakClient keycloakClient, UtenteShopRepository utenteShopRepository) {
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
            throw new RuntimeException("Login failed with status: " + responseEntity.getStatusCode());
        }
    }

    @Transactional
    public UtenteShop createUtenteInKeycloak(UtenteShop utenteShop) {
        if (utenteShop.getFirstName() == null || utenteShop.getLastName() == null || utenteShop.getUsername() == null) {
            throw new IllegalArgumentException("I campi obbligatori non sono valorizzati.");
        }

        // Ottieni il token di amministrazione
        String accessToken = getAdminAccessToken();
        String authorizationHeader = "Bearer " + accessToken;

        // Controlla se l'utente esiste già in Keycloak
        System.out.println("Verifica se l'utente esiste già in Keycloak...");
        List<UserRepresentation> existingUsers = keycloakClient.getUsersByUsername(authorizationHeader, utenteShop.getUsername());
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("L'utente esiste già in Keycloak.");
        }

        // Creazione dell'utente su Keycloak
        System.out.println("Creazione dell'utente in Keycloak...");
        UtenteKeycloak utenteKeycloak = convertToUtenteKeycloak(utenteShop);
        ResponseEntity<Object> response = keycloakClient.createUsers(authorizationHeader, utenteKeycloak);

        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Errore durante la creazione dell'utente in Keycloak.");
        }
        System.out.println("Utente creato in Keycloak con successo.");

        // Recupera il Keycloak ID
        String location = response.getHeaders().get("location").get(0);
        String[] locationParts = location.split("/");
        String keycloakId = locationParts[locationParts.length - 1];
        utenteShop.setKeycloakId(keycloakId);

        // Salva l'utente nel database
        System.out.println("Salvo l'utente nel database...");
        UtenteShop savedUtenteShop = utenteShopRepository.save(utenteShop);
        System.out.println("Utente salvato nel database: " + savedUtenteShop);

        // Assegna ruoli su Keycloak
        System.out.println("Assegnazione del ruolo...");
        assignRolesToUser(authorizationHeader, keycloakId, utenteShop.getRole());
        System.out.println("Ruolo assegnato con successo.");

        return savedUtenteShop;
    }

    private void assignRolesToUser(String authorizationHeader, String userId, String role) {
        ResponseEntity<List<RoleKeycloak>> rolesResponse = keycloakClient.getAvailableRoles(authorizationHeader, userId, "0", "100");
        List<RoleKeycloak> roleList = rolesResponse.getBody();

        Optional<RoleKeycloak> selectedRoleOpt = roleList.stream()
                .filter(r -> r.getRole().equals(role))
                .findFirst();

        if (selectedRoleOpt.isEmpty()) {
            throw new RuntimeException("Ruolo non trovato: " + role);
        }

        RoleKeycloak selectedRole = selectedRoleOpt.get();
        String clientIdRole = selectedRole.getClientId();

        List<RoleRepresentation> rolesToAssign = List.of(selectedRole.toRoleRepresentation());
        keycloakClient.addRoleToUser(authorizationHeader, userId, clientIdRole, rolesToAssign);
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




    public List<UserRepresentation> getAllUsers() {
        String accessToken = getAdminAccessToken();
        return keycloakClient.getAllUsers("Bearer " + accessToken);
    }


    public void logout(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token mancante o non valido");
        }

        // Estrai il token dal header
        String accessToken = authorization.split(" ")[1];

        try {
            // Ottieni il refresh token corrispondente (opzionale, dipende dalla configurazione)
            String refreshToken = extractRefreshToken(accessToken);

            // Chiamata a Keycloak per invalidare i token
            ResponseEntity<Void> response = keycloakClient.logout(refreshToken, clientIdAdmin, clientSecretAdmin);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Errore nella revoca dei token in Keycloak");
            }
            System.out.println("Logout eseguito con successo per il token: " + accessToken);

        } catch (Exception e) {
            System.err.println("Errore durante il logout: " + e.getMessage());
            throw new RuntimeException("Errore durante il logout", e);
        }
    }

    private String extractRefreshToken(String accessToken) {
        // Decodifica del token JWT per estrarre il refresh token, se necessario
        String refreshToken = ""; // Implementazione specifica, dipende dalla configurazione
        return refreshToken;
    }




}
