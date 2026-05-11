package pe.gob.onpe.sceorcbackend.utils;

public class ConstantesMensajes {

    public static final String TOKEN_INVALIDO_NO_COINCIDE_TOKEN_ACTIVO_REDIS = "Token Inválido, no coincide con el token activo. Vuelva a iniciar sesión.";

    private ConstantesMensajes() {
    }

    public static final String MSJ_FORMAT_NO_CONFIGURADO_AE_PARA_LA_COPIA="No está configurado el Acta de Escrutinio para la copia %s.";

    public static final String MSJ_FORMAT_NUMERO_MESA_NO_EXISTE = "El número de mesa %s no se encuentra registrado.";

    public static final String MESAJE_NO_CONFIGURADO_PROCESO_ELECTORAL = "No esta configurado el proceso electoral.";

    public static final String MSJ_FORMAT_COPIA_DIGITO_NO_EXISTEN = "La copia y dígito %s no coinciden con lo registrado en el sistema.";

    public static final String MSJ_FORMAT_NO_CONFIGURADO_ACTA_ESCRUTINIO = "No está configurado el acta de escrutinio para la copia %s.";
    public static final String MSJ_FORMAT_NO_CONFIGURADO_ACTA_AIS = "No está configurado el acta de instalación y sufragio para la copia %s.";

    public static final String MSJ_FORMAT_MESA_6_CARACTERES =  "La mesa %s debe contener "+ConstantesComunes.LONGITUD_MESA+" caracteres.";


    public static final String MSJ_FORMAT_COPIA_Y_MESA_VACIAS = "El nro de mesa y copia no debe ser vacío.";

    // CONSTANTES PARA RESPALDO
    public static final String MSJ_RESPALDO_NOMBRE_CLASE = "RespaldoServiceImpl";
    public static final String MSJ_RESPALDO_LOG_BACKUP_FAIL = "No se pudo completar el backup de base de datos.";
    public static final String MSJ_RESPALDO_LOG_BACKUP_DONE = "Se realizó el backup de base de datos.";
    public static final String MSJ_RESPALDO_LOG_RESTORE_FAIL = "No se pudo completar el restore del backup.";
    public static final String MSJ_RESPALDO_LOG_RESTORE_DONE = "Se realizó la restauración del backup.";
    public static final String MSJ_RESPALDO_LOG_ARCHIVO_NO_ENCONTRADO = "No se encontró el archivo o esta vacío.";


    public static final String MJS_ARCHIVO_NO_SUPERA_10_MB = "El archivo no debe superar los 10 MB.";
    public static final String MJS_ARCHIVO_NO_SUPERA_5_MB = "El archivo no debe superar los 5 MB.";
    public static final String MJS_ARCHIVO_NO_SUPERA_50_MB = "El archivo no debe superar los 50 MB.";
}
