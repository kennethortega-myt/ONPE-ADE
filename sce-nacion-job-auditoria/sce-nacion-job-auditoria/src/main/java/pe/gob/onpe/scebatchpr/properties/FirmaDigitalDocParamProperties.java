package pe.gob.onpe.scebatchpr.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.ext.firmadoc.param")
public class FirmaDigitalDocParamProperties {

	private String signatureFormat;
    private String signatureLevel;
    private String signaturePackaging;
    private String webTsa;
    private String userTsa;
    private String passwordTsa;
    private String contactInfo;
    private String signatureReason;
    private int signatureStyle;
    private int stampTextSize;
    private int stampWordWrap;
    private int stampPage;
    private int positionx;
    private int positiony;
    private boolean certificationSignature;
	
}
