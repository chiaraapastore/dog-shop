package dogshop.market.client;
import dogshop.market.entity.TokenRequest;
import dogshop.market.entity.UtenteKeycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@FeignClient(name = "keycloakClient", url = "${keycloak.auth-server-url}")
public interface KeycloakClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "/realms/${keycloak.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Object> getAccessToken(@RequestBody TokenRequest tokenRequest);

    @PostMapping("/realms/${keycloak.realm}/protocol/openid-connect/token")
    ResponseEntity<Map<String, Object>> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret);

    @RequestMapping(method = RequestMethod.POST,
            value = "/admin/realms/${keycloak.realm}/users",
            produces = "application/json")
    ResponseEntity<Object> createUsers(@RequestHeader("Authorization") String accessToken,
                                       @RequestBody UtenteKeycloak utenteKeycloak);

    @GetMapping("/admin/realms/${keycloak.realm}/users")
    List<UserRepresentation> getUsersByUsername(@RequestHeader("Authorization") String token,
                                                @RequestParam("username") String username);

    @GetMapping("/admin/realms/${keycloak.realm}/ui-ext/available-roles/users/{id}")
    ResponseEntity<List<RoleKeycloak>> getAvailableRoles(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("id") String id,
            @RequestParam(value = "first", defaultValue = "0") String first,
            @RequestParam(value = "max", defaultValue = "100") String max);

    @RequestMapping(method = RequestMethod.POST,
            value = "/admin/realms/${keycloak.realm}/users/{id}/role-mappings/clients/{clientIdRole}",
            produces = "application/json")
    ResponseEntity<Object> addRoleToUser(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable String id,
            @PathVariable String clientIdRole,
            @RequestBody List<RoleRepresentation> roles);

    @GetMapping("/admin/realms/${keycloak.realm}/users")
    List<UserRepresentation> getAllUsers(@RequestHeader("Authorization") String token);
}
