package dogshop.market.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class TokenRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;

    @NotBlank(message = "Client ID is required")
    private String client_id;
    private String client_secret;

    @NotBlank(message = "Grant type is required")
    private String grant_type;

    public TokenRequest(String username, String password, String client_id, String client_secret, String grant_type) {
        this.username = username;
        this.password = password;
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.grant_type = grant_type;
    }

    public @NotBlank(message = "Client ID is required") String getClient_id() {
        return client_id;
    }
    public @NotBlank(message = "Client Secret is required") String getClient_secret() {
        return client_secret;
    }
    public @NotBlank(message = "Grant type is required") String getGrant_type() {
        return grant_type;
    }

    public @NotBlank(message = "Password is required") @Size(min = 6, message = "Password should be at least 6 characters") String getPassword() {
        return password;
    }

    public @NotBlank(message = "Username is required") String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }
}

