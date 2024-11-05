package dogshop.market.service;

import dogshop.market.client.KeycloakClient;
import dogshop.market.client.RoleKeycloak;
import dogshop.market.entity.TokenRequest;
import dogshop.market.entity.UtenteShop;
import dogshop.market.entity.UtenteKeycloak;
import dogshop.market.repository.UtenteShopRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Autowired
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
    public UtenteShop createUtenteInKeycloak(UtenteShop utenteShop) throws FeignException {

        String accessToken = getAdminAccessToken();
        String authorizationHeader = "Bearer " + accessToken;

        List<UserRepresentation> existingUsers = keycloakClient.getUsersByUsername(authorizationHeader, utenteShop.getEmail());
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("Email già esistente in Keycloak: " + utenteShop.getEmail());
        }

        UtenteKeycloak utenteKeycloak = convertToUtenteKeycloak(utenteShop);

        ResponseEntity<Object> response = keycloakClient.createUsers(authorizationHeader, utenteKeycloak);
        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Errore durante la creazione dell'utente in Keycloak: " +
                    (response != null ? response.getBody() : "Nessuna risposta dal server."));
        }

        String location = response.getHeaders().get("location").get(0);
        String[] locationParts = location.split("/");
        String keycloakId = locationParts[locationParts.length - 1];

        utenteShop.setKeycloakId(keycloakId);


        UtenteShop savedUtenteShop = utenteShopRepository.save(utenteShop);
        assignRolesToUser(authorizationHeader, keycloakId, utenteShop.getRole());

        return savedUtenteShop;
    }


    private void assignRolesToUser(String authorizationHeader, String userId, String role) {
        try {
            ResponseEntity<List<RoleKeycloak>> rolesResponse = keycloakClient.getAvailableRoles(
                    authorizationHeader,
                    userId,
                    "0",
                    "100");

            List<RoleKeycloak> roleList = rolesResponse.getBody();

            // Log dei ruoli disponibili
            System.out.println("Ruoli disponibili per il client:");
            roleList.forEach(r -> System.out.println("Ruolo: " + r.getRole()));

            Optional<RoleKeycloak> selectedRoleOpt = roleList.stream()
                    .filter(r -> r.getRole().equals(role))
                    .findFirst();

            if (selectedRoleOpt.isEmpty()) {
                throw new RuntimeException("Ruolo non trovato: " + role + ". Ruoli disponibili: " +
                        roleList.stream().map(RoleKeycloak::getRole).collect(Collectors.joining(", ")));
            }

            RoleKeycloak selectedRole = selectedRoleOpt.get();
            String clientIdRole = Optional.ofNullable(selectedRole.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client ID non trovato per il ruolo: " + selectedRole.getRole()));

            List<RoleRepresentation> rolesToAssign = List.of(selectedRole.toRoleRepresentation());
            ResponseEntity<Object> addRoleResponse = keycloakClient.addRoleToUser(
                    authorizationHeader, userId, clientIdRole, rolesToAssign);

            if (addRoleResponse == null || !addRoleResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Errore durante l'assegnazione del ruolo: " +
                        (addRoleResponse != null ? addRoleResponse.getBody() : "Nessuna risposta dal server."));
            }

            System.out.println("Ruolo assegnato con successo all'utente con ID: " + userId);

        } catch (FeignException e) {
            throw new RuntimeException("Errore durante l'assegnazione dei ruoli: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Errore generale durante l'assegnazione dei ruoli: " + e.getMessage(), e);
        }
    }





    private UtenteKeycloak convertToUtenteKeycloak(UtenteShop utenteShop) {
        UtenteKeycloak keycloak = new UtenteKeycloak();
        keycloak.setUsername(utenteShop.getUsername());
        keycloak.setFirstName(utenteShop.getFirstName());
        keycloak.setLastName(utenteShop.getLastName());
        keycloak.setEmail(utenteShop.getEmail());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(utenteShop.getPassword());
        credentialRepresentation.setTemporary(false);

        keycloak.setCredentials(List.of(credentialRepresentation));
        keycloak.setEnabled(false); // Imposto a true se voglio abilitare l'account immediatamente

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



}
