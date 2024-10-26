package com.example.dogshop.config;

import org.springframework.context.annotation.Configuration;

import java.util.Optional;

//Classe di configurazione per le proprietà legate al token. Memorizza informazioni come resourceId e principalAttribute.
@Configuration
public class TokenConverterPropierties {

    private String resourceId;
    private String principalAttribute;

    public String getResourceId() {
        return resourceId;
    }
    public Optional<String> getPrincipalAttribute() {
        return Optional.ofNullable(principalAttribute);
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    public void setPrincipalAttribute(String principalAttribute) {
        this.principalAttribute = principalAttribute;
    }


}
