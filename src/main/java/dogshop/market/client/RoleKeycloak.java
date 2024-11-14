package dogshop.market.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleKeycloak {
    private String id;
    private String role;
    private String client;
    private String clientId;
    private String description;

    public RoleRepresentation toRoleRepresentation(){
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setId(this.id);
        roleRepresentation.setName(this.role);
        roleRepresentation.setDescription(this.description);
        return roleRepresentation;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
