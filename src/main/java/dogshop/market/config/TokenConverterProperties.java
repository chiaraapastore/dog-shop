package dogshop.market.config;

import org.springframework.context.annotation.Configuration;

import java.util.Optional;


@Configuration
public class TokenConverterProperties {
    private String resourceId;
    private String principalAttribute;
    public String getResourceId() {
        return resourceId;
    }

}
