package pe.gob.onpe.scebackend.utils.constantes;

public class ConstantesMensajes {

    public static final String TOKEN_INVALIDO_NO_COINCIDE_TOKEN_ACTIVO_REDIS = "Token Inválido, no coincide con el token activo. Vuelva a iniciar sesión.";

    private ConstantesMensajes() {
    }
    // CONSTANTES PARA RESPALDO
    public static final String MSJ_RESPALDO_NOMBRE_CLASE = "RespaldoServiceImpl";
    public static final String MSJ_RESPALDO_LOG_BACKUP_FAIL = "No se pudo completar el backup de base de datos.";
    public static final String MSJ_RESPALDO_LOG_BACKUP_DONE = "Se realizó el backup de base de datos.";
    public static final String MSJ_RESPALDO_LOG_RESTORE_FAIL = "No se pudo completar el restore del backup.";
    public static final String MSJ_RESPALDO_LOG_RESTORE_DONE = "Se realizó la restauración del backup.";
    public static final String MSJ_RESPALDO_LOG_ARCHIVO_NO_ENCONTRADO = "No se encontró el archivo o esta vacío.";
}
