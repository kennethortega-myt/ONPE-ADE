package pe.gob.onpe.sceorcbackend.utils;

public class ConstantesOficio {

    private ConstantesOficio() {
    }

    public static final String ESTADO_OFICIO_PENDIENTE = "P";
    public static final String ESTADO_OFICIO_NACION = "N";
    public static final String ESTADO_OFICIO_RED = "R";
    public static final String ESTADO_OFICIO_JURADO = "J";
    public static final String ESTADO_OFICIO_EXPEDIENTE = "G";
    public static final String ESTADO_OFICIO_ATENTIDO = "A";
    public static final String ESTADO_OFICIO_RECHAZADA = "H";

    public static final String TIPO_SOBRE_PLOMO = "PLOMO";
    public static final String TIPO_SOBRE_CELESTE = "CELESTE";

    public static final String ASUNTO_OFICIO_BODY = "Remite Actas Electorales Observadas del proceso";

    public static final String TIPO_DOCUMENTO_OFICIO = "OFICIO";
    public static final String TIPO_DOCUMENTO_CARGO = "CARGO";
    public static final String TIPO_DOCUMENTO_RESOLUCION_JNE = "RESOLUCION";

    // ================= MENSAJES =================
    public static final String MSG_ACTA_NO_VALIDA = "La acta no cumple con los requisitos para generar el oficio.";
    public static final String MSG_ACTAS_NO_VALIDAS = "Ninguna de las actas cumple con los requisitos para generar el oficio.";
    public static final String MSG_ARCHIVO_NO_ENCONTRADO = "No se encontró el archivo del oficio.";
    public static final String MSG_OFICIO_SIN_ARCHIVO = "El oficio no cuenta con archivo registrado.";
    public static final String MSG_ARCHIVO_NO_EXISTE = "El archivo no existe.";
    public static final String MSG_ARCHIVO_NO_SAVE = "No se pudo guardar el archivo del oficio.";
    public static final String MSG_NO_JEE = "No se encontró Jurado Electoral Especial para código de CC = ";
    public static final String MSG_ERROR_PDF = "Error al generar el PDF del oficio ";
    public static final String MSG_SOBRE_NO_ENCONTRADO = "No se encontró la imagen del acta del sobre ";
    public static final String MSG_SOBRE_NO_ENCONTRADO_STAE = "No se encontró la imagen del acta STAE del sobre ";
    public static final String MSG_ERROR_SOBRE = "Error al obtener archivos del sobre: ";
    public static final String MSG_OFICIO_ACTA_NO_ENCONTRADO = "No se encontró un oficio asociado al acta con ID: ";
    public static final String MSG_OFICIO_NO_ENCONTRADO = "No se encontró un oficio.";
    public static final String MSG_ACTA_NO_ENCONTRADA = "No se encontró acta asociado con ID: ";
    public static final String MSG_OFICIO_ENVIADO = "El oficio ya se encuentra en estado Enviado.";
    public static final String MSG_ACTA_ENVIADA = "La acta ya se encuentra en estado Enviado.";
    public static final String MSG_OFICIO_EXITOSO = "El oficio fue transmitido exitosamente a Nación.";
    public static final String MSG_ERROR_TRANSMISION = "Ocurrió un error al transmitir el oficio: ";
    public static final String MSG_TIPO_DOC_INVALIDO = "Tipo de documento no válido.";
    public static final String MSG_DATOS_INVALIDOS = "Datos de acta no válidos.";
    public static final String MSG_ERROR_INTERNO = "Ocurrió un error interno. Contacte al administrador.";
    public static final String MSG_NO_CC = "No se encontró centro de cómputo con código: ";
    public static final String MSG_ERROR_SEGUIMIENTO = "Error en obtener seguimiento.";
    public static final String MSG_ERROR_BASE64 = "Error al convertir archivo en Base64.";
    public static final String MSG_NO_ARCHIVO_CARGO = "No se encontró el archivo del cargo de entrega.";
    public static final String MSG_NO_CARGO = "No se encontró un cargo de entrega generado.";
    public static final String MSG_NO_OFICIO = "No se encontró un oficio generado.";
    public static final String MSG_NO_DET_OFICIO = "No se encontró un detalle del oficio.";
    public static final String MSG_NO_RESOLUCION_JNE = "No se encontró un resolución enviada por el JNE.";
    public static final String MSG_NO_ARCHIVO_RESOLUCION_JNE = "No se encontró el archivo de la resolución enviada por el JNE.";
    public static final String MSG_NO_SAVE_RESOLUCION_JNE = "No se pudo guardar el archivo enviado por el JEE : %s";
    public static final String MSG_RESOLUCION_JNE_OK = "La resolución JEE %s fue procesada correctamente.";
    public static final String MSG_NUMERO_RESOLUCION_JNE_FOUND = "El número de resolución JEE %s ya se encuentra registrado.";
}
