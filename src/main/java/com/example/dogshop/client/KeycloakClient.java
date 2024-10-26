package com.example.dogshop.client;
import com.example.dogshop.entity.UtenteKeycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "keycloakClient", url = "${keycloak.auth-server-url}")
public interface KeycloakClient {

    @PostMapping("/realms/{realm}/protocol/openid-connect/token")
    ResponseEntity<Map<String, Object>> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret);

    @PostMapping("/admin/realms/{realm}/users")
    ResponseEntity<Object> createUsers(@RequestHeader("Authorization") String token,
                                       @PathVariable("realm") String realm,
                                       @RequestBody UtenteKeycloak utente);

    @GetMapping("/admin/realms/{realm}/users")
    List<UserRepresentation> getUsersByUsername(@RequestHeader("Authorization") String token,
                                                @PathVariable("realm") String realm,
                                                @RequestParam("username") String username);

    @GetMapping("/admin/realms/{realm}/users/{userId}/role-mappings/realm/available")
    ResponseEntity<List<RoleRepresentation>> getAvailableRoles(@RequestHeader("Authorization") String token,
                                                               @PathVariable("realm") String realm,
                                                               @PathVariable("userId") String userId);

    @PostMapping("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    void addRoleToUser(@RequestHeader("Authorization") String token,
                       @PathVariable("realm") String realm,
                       @PathVariable("userId") String userId,
                       @RequestBody RoleRepresentation role);
}
