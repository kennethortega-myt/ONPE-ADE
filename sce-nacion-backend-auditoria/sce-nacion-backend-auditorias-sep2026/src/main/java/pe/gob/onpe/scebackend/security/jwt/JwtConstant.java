package pe.gob.onpe.scebackend.security.jwt;

public class JwtConstant {

	private JwtConstant() {

	}

    public static final String KEY_VALIDATOR = "wOhb0ceP3hSOf0Lg";

    public static final String AUTHENTICATION_URL = "/api/auth/login";
	public static final String REFRESH_TOKEN_URL = "/usuario/refreshToken";
    public static final String AUTHENTICATION_CLIENT = "/api/auth/client-token";
    public static final String AUTHENTICATION_EXTERNAL = "/api/auth/external-login";
    public static final String API_GOOGLE_RECAPTCHA = "/api/apigoogle";
	public static final String API_PUBLIC_KEY =  "/api/auth/public-key";

    public static String[] getWhitelistPaths() {
        return new String[]{
        		AUTHENTICATION_URL,
        		AUTHENTICATION_CLIENT,
        		AUTHENTICATION_EXTERNAL,
        		API_GOOGLE_RECAPTCHA,
				API_PUBLIC_KEY,
        		"/ws/**",
        		"/usuario/cerrar-sesion",
        		"/actuator/**",
        		"/swagger-ui/**",
        		"/error/**",
        		"/exportar/",
        		"/exportar-pr/",
        		"/acta-exportacion/cc/**",
        		"/mesa-exportacion/cc/**",
        		"/padron-electoral/exportacion/**",
        		"/transmision/recibir-transmision/",
        		"/reseteo-transmision/recibir-transmision/",
        		"/transmision-pc/recibir-puesta-cero/",
				"/consulta/**",
				"/centroComputo/**",
				"/autorizacion/recibir-autorizacion",
				"/autorizacion/crear-solicitud-autorizacion",
				"/proceso-electoral/acronimo/",
				"/usuario/cerrar-sesion-activa"
        };
    }

}
