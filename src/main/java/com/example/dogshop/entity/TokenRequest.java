package com.example.dogshop.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenRequest {
    private String username;
    private String password;
    private String client_id;
    private String client_secret;
    private String grant_type;

    public TokenRequest(String username, String password, String client_id, String client_secret, String grant_type) {
        this.username = username;
        this.password = password;
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.grant_type = grant_type;
    }

}

