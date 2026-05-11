package pe.gob.onpe.sceorcbackend.utils;

public class ConstantesOtrosDocumentos {

    private ConstantesOtrosDocumentos() {
    }

    //ESTADO DIGITALIZACION DOCUMENTO SEGUN CATALOGO  mae_estado_digitalizacion_otro_documento - c_estado_digitalizacion
    public static final String ESTADO_DIGTAL_DIGITALIZADO = "D";
    public static final String ESTADO_DIGTAL_APROBADO = "C";
    public static final String ESTADO_DIGTAL_RECHAZADO = "R";
        public static final String ESTADO_DIGTAL_REDIGITALIZADO = "Z";

    //ESTADO DOCUMENTO SEGUN CATALOGO  mae_estado_otro_documento - c_estado_documento
    public static final String ESTADO_DOC_SIN_PROCESAR = "N";
    public static final String ESTADO_DOC_EN_PROCESO = "D"; //ASOCIADAS
    public static final String ESTADO_DOC_PROCESADO = "P";
    public static final String ESTADO_DOC_ANULADO = "E";

    public static final String TIPO_DOC_HOJA_ASISTENCIA = "HA";
    public static final String TIPO_DOC_LISTA_ELECTORES = "LE";

    public static final String TIPO_PERDIDA_PARCIAL = "S";
    public static final String TIPO_PERDIDA_TOTAL="L";
}
