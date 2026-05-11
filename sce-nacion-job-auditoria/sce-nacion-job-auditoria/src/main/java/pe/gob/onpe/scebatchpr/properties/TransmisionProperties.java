package pe.gob.onpe.scebatchpr.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "app.sce-batch-pr.transmision")
public class TransmisionProperties {

	private String host;
	private int port;
	private String username;
	private String password;
	private String sendQueue;
	private String replyQueue;
	
}
