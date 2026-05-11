package pe.gob.onpe.sceorcbackend.security.jwt;

public class JwtConstant {

    private JwtConstant(){

    }

    public static final String KEY_VALIDATOR = "wOhb0ceP3hSOf0Lg";
    public static final String KEY = "2g@{4R:BE[e474]7";

    public static final String AUTHENTICATION_URL = "/api/auth/login";
    public static final String REFRESH_TOKEN_URL = "/usuario/refreshToken";
    public static final String API_PUBLIC_KEY =  "/api/auth/public-key";
    public static final String API_GOOGLE_RECAPTCHA = "/api/apigoogle";
    public static final String[] API_LIBRES = new String[] {
            AUTHENTICATION_URL,
            API_GOOGLE_RECAPTCHA,
            API_PUBLIC_KEY,
            "/ws/**",
            "/actuator/**",
    		"/swagger-ui/**",
    		"/error/**",
            "/digitization/approveMesaModelo",
    		"/stae/acta",
    		"/stae/lista-electores",
    		"/stae/documentos-electorales",
    		"/parametro-conexion-cc/ping",
    		"/extranjero/uploadActaDigitization",
            "/jneRecepcion/resolucionOficio",
            "/usuario/cerrar-sesion-activa",
            "/prediction/procesar-resultado"
    };
}
