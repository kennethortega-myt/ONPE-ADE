package pe.gob.onpe.scebatchpr.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sce-nacion")
public class SceNacionProperties {
    private String url;
    private String endpoint;
    private String endpointClientToken;
    private String clientId;
    private String clientSecret;

    public String getEndpointUrl() {
        return url + endpoint;
    }

    public String getEndpointClientTokenUrl() {
        return url + endpointClientToken;
    }
}
