package pe.gob.onpe.scebatchpr.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.ext.firmadoc.config")
public class FirmaDigitalDocConfigProperties {

	private String url;
	private String credential;
	
}
