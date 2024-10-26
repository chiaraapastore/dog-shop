package com.example.dogshop.service;

import com.example.dogshop.client.KeycloakClient;
import com.example.dogshop.entity.Utente;
import com.example.dogshop.entity.UtenteKeycloak;
import com.example.dogshop.repository.UtenteRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakClient keycloakClient;
    private final UtenteRepository utenteRepository;

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
        return keycloakClient.login(username, password, clientIdAdmin, clientSecretAdmin)
                .getBody().get("access_token").toString();
    }

    public ResponseEntity<Object> createUserInKeycloak(UtenteKeycloak utente) throws FeignException {
        String accessToken = getAdminAccessToken();
        String authorizationHeader = "Bearer " + accessToken;

        ResponseEntity<Object> response = keycloakClient.createUsers(authorizationHeader, realm, utente);
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            String userId = extractUserId(response);
            assignRoleToUser(authorizationHeader, userId, utente.getRole());

            Utente savedUser = new Utente();
            savedUser.setUsername(utente.getUsername());
            savedUser.setEmail(utente.getEmail());
            savedUser.setRole(utente.getRole());
            utenteRepository.save(savedUser);

            return response;
        } else {
            throw new RuntimeException("Errore nella creazione dell'utente: " + response.getBody());
        }
    }

    private String extractUserId(ResponseEntity<Object> response) {
        String location = response.getHeaders().get("location").get(0);
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private void assignRoleToUser(String authorizationHeader, String userId, String role) {
        ResponseEntity<List<RoleRepresentation>> rolesResponse = keycloakClient.getAvailableRoles(authorizationHeader, realm, userId);
        Optional<RoleRepresentation> roleOpt = rolesResponse.getBody().stream()
                .filter(r -> r.getName().equals(role))
                .findFirst();

        if (roleOpt.isPresent()) {
            keycloakClient.addRoleToUser(authorizationHeader, realm, userId, roleOpt.get());
        } else {
            throw new RuntimeException("Ruolo non trovato: " + role);
        }
    }

    public String getAdminAccessToken() {
        return login(adminUsername, adminPassword);
    }

    public Utente getByUsername(String username) {
        String accessToken = getAdminAccessToken();
        String authorizationHeader = "Bearer " + accessToken;

        Optional<Utente> userOpt = utenteRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        List<UserRepresentation> users = keycloakClient.getUsersByUsername(authorizationHeader, realm, username);
        if (users.isEmpty()) {
            throw new RuntimeException("User not found in Keycloak with username: " + username);
        }

        UserRepresentation userRep = users.get(0);
        Utente utente = new Utente();
        utente.setUsername(userRep.getUsername());
        utente.setEmail(userRep.getEmail());

        utenteRepository.save(utente);
        return utente;
    }
}
