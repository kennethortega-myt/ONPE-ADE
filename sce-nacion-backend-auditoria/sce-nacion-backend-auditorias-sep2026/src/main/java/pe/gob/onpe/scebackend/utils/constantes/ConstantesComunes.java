package pe.gob.onpe.scebackend.utils.constantes;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ConstantesComunes {

    public static final String ALGORITHM_SHA_256 = "SHA-256";
    public static final String CODIGO_PROCESO = "56";
    public static final String MSJ_ERROR = "Error:";

    public static final String GUION_MEDIO = "-";
    public static final String GUION_BAJO = "_";
    public static final String SALTO_LINEA = "\n";
    public static final String DOS_PUNTOS = ":";

    private ConstantesComunes() {
        throw new UnsupportedOperationException("ConstantesComunes es una clase utilitaria y no puede ser instanciada");
    }

    public static final String USER_TOKEN_PREFIX = "user:";
    public static final String TOKEN_SUFFIX = ":token";
    public static final String REFRESH_TOKEN_SUFFIX = ":refresh-token";
    public static final String BLACKLISTED_VALUE = "blacklisted";
    public static final String TOKEN_INVALIDO = "Token Inválido";

    public static final String ACTION_INSERT = "INSERT";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_UPSERT = "UPSERT";

    public static final String OPTION_1ER_CONTROL_CALIDAD_RESOLUCION = "1CCRESOL";
    public static final String OPTION_ASOCIAR_RESOLUCION = "ASOCIAR_RESOL";
    public static final String OPTION_APLICAR_RESOLUCION = "APLICAR_RESOL";

    public static final String ESTADO_ACTA_ENVIADA_A_JEE = "I";
    public static final String ESTADO_ACTA_PARA_ENVIO_AL_JURADO = "H";

    public static final String ESTADO_ACTA_PROCESADA = "D";
    public static final String ESTADO_ACTA_EXTRAVIADA = "O";
    public static final String ESTADO_ACTA_SINIESTRADA = "S";
    public static final String ESTADO_ACTA_MESA_NO_INSTALADA = "N";
    public static final String ESTADO_ACTA_REPROCESADA_NORMAL = "Q";
    public static final String ESTADO_ACTA_REPROCESADA_ANULADA = "R";
    public static final String ESTADO_ACTA_ANULADA = "M";

    public static final String ESTADO_ACTA_PROCESADA_RESOLUCION = "L";

    public static final String PATH_IMAGE_COMMON_NAC = "pe/gob/onpe/scebackend/common/image/";
    public static final String PATH_IMAGE_COMMON = "pe/gob/onpe/scebackend/common/image/";

    public static final String PATH_REPORT_JRXML = "pe/gob/onpe/scebackend/reportes";

    public static final String PATH_REPORT_PUESTA_CERO_JRXML = "pe/gob/onpe/scebackend/reportes/puesta-cero";

    public static final String PUESTA_CERO_REPORT_JRXML = "PuestaCeroNac.jrxml";
    public static final String AVANCE_MESA_REPORT_JRXML = "AvanceMesa.jrxml";
    public static final String AVANCE_MESA_CPR_REPORT_JRXML = "AvanceMesaCPR.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML = "AvanceMesaPreferencial.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_9 = "AvanceMesaPreferencial_9.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_18 = "AvanceMesaPreferencial_18.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_27 = "AvanceMesaPreferencial_27.jrxml";
    public static final String AVANCE_ESTADO_ACTAS_REPORT_JRXML = "AvanceEstadoActas.jrxml";
    public static final String ORGANIZACIONES_POLITICAS = "OrganizacionesPoliticas.jrxml";
    public static final String REPORTE_MESAS_ESTADO_MESA = "ReporteMesasEstadoMesa.jrxml";
    public static final String REPORTE_MESAS_ESTADO_MESA_X_ESTADO = "ReporteMesasEstadoMesaxEstado.jrxml";
    public static final String REPORTE_MESAS_ESTADO_ACTA = "ReporteMesasEstadoActa.jrxml";
    public static final String REPORTE_MESAS_ESTADO_ACTA_X_ESTADO = "ReporteMesasEstadoActaxEstado.jrxml";
    public static final String REPORTE_MESAS_ESTADO_DIGITACION = "ReporteMesasEstadoDigitacion.jrxml";
    public static final String REPORTE_MESAS_ESTADO_DIGITACION_X_ESTADO = "ReporteMesasEstadoDigitacionxEstado.jrxml";
    public static final String REPORTE_AVANCE_DIGITALIZACION_ACTAS = "AvanceDigitalizacion.jrxml";
    public static final String LISTA_MESAS_UBIGEO_REPORT_JRXML = "ListaMesasUbigeo.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML = "ResultadoActasContabilizadas.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_9 = "ResultadoActasContabilizadas_9.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_18 = "ResultadoActasContabilizadas_18.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_27 = "ResultadoActasContabilizadas_27.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_36 = "ResultadoActasContabilizadas_36.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_CPR_REPORT_JRXML = "ResultadoActasContabilizadas_CPR.jrxml";
    public static final String REPORTE_INFORMACION_OFICIAL = "InformacionOficial.jrxml";
    public static final String REPORTE_MESAS_OBSERVACIONES = "RPT060310-41-ReporteMesasObservaciones.jrxml";
    public static final String REPORTE_RELACION_PUESTA_CERO = "RelacionPuestaCero.jrxml";
    public static final String REPORTE_DETALLE_AVANCE_REGISTRO_UBIGEO = "DetalleAvanceRegistroUbigeo.jrxml";
    public static final String REPORTE_TOTAL_ACTAS_ENVIADAS_JEE_CENTROCOMPUTO = "TotalActasEnviadasJEECentroComputo.jrxml";
    public static final String REPORTE_AVANCE_MESA_POR_MESA = "AvanceMesaPorMesa.jrxml";
    public static final String REPORTE_AVANCE_MESA_POR_MESA_REVOCATORIA = "AvanceMesaPorMesaRevocatoria.jrxml";
    public static final String REPORTE_TRANSACCIONES_REALIZADAS = "TransaccionesRealizadas.jrxml";
    public static final String REPORTE_CIFRA_REPARTIDORA = "CifraRepartidora.jrxml";

    public static final String RESUMEN_TOTAL_CIFRAS_REPORT_JRXML = "ResumenTotalCifras.jrxml";
    public static final String RESUMEN_TOTAL_PORCENTAJES_REPORT_JRXML = "ResumenTotalPorcentajes.jrxml";
    public static final String REPORTE_ACTAS_DIGITALIZADAS = "ActasDigitalizadas.jrxml";
    public static final String REPORTE_ACTAS_DIGITALIZADAS_EXCEL = "ActasDigitalizadasExcel.jrxml";
    public static final String REPORTE_ESTADO_ACTAS_ODPE = "EstadoActasOdpe.jrxml";

    public static final String REPORTE_AUDITORIA_DIGITACION = "ReporteAuditoriaDigitacion.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL = "ReporteAuditoriaDigitacionPreferencial_34.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_9 = "ReporteAuditoriaDigitacionPreferencial_9.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_16 = "ReporteAuditoriaDigitacionPreferencial_16.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_27 = "ReporteAuditoriaDigitacionPreferencial_27.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_REVOCATORIA = "ReporteAuditoriaDigitacion-CPR.jrxml";

    public static final String REPORTE_ACTAS_NO_DEVUELTAS_ACTAS = "ActasNoDevueltas-acta.jrxml";
    public static final String REPORTE_ACTAS_NO_DEVUELTAS_TODAS = "ActasNoDevueltas-todas.jrxml";
    public static final String REPORTE_LISTA_PARTICIPANTES = "ListaParticipantes.jrxml";
    public static final String REPORTE_OMISOS_DETALLE = "RPT06030101_ListaOmisosDetalle.jrxml";
    public static final String REPORTE_OMISOS_RESUMEN = "RPT06030102_ListaOmisosResumen.jrxml";
    public static final String REPORTE_AUTORIDADES_REVOCADAS = "AutoridadesRevocadas.jrxml";
    public static final String REPORTE_ASISTENCIA_MIEMBROS_MESA = "AsistenciaMiembroMesa.jrxml";
    public static final String REPORTE_ASISTENCIA_PERSONEROS = "AsistenciaPersoneros.jrxml";
    public static final String REPORTE_ASISTENCIA_MM_ESCRUTINIO = "AsistenciaMMEscrutinio.jrxml";
    public static final String REPORTE_PROCEDE_PAGO = "RPT60316-44-ReporteProcedePago.jrxml";
    public static final String REPORTE_COMPARACION_OMISOS_AUSENTISMO = "RPT06030603-TipoComparacionTodos.jrxml";
    public static final String REPORTE_RECEPCION = "Recepcion.jrxml";
    public static final String REPORTE_MESAS_SIN_OMISOS = "MesasSinOmisos.jrxml";
    public static final String REPORTE_AVANCE_DIGITALIZACION_RESOLUCION = "AvanceDigitalizacionResolucion.jrxml";
    public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_CONTROL_DIGITALIZACION = "PrecisionAsisteAutomaControlDigitalizacion.jrxml";
    public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_RESUMEN = "PrecisionAsisteAutomaDigitacionResumen.jrxml";
    public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_DETALLE_PRESIDENCIAL = "PrecisionAsisteAutomaDigitacionDetallePresidencial.jrxml";
    public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_DETALLE_NO_PRESIDENCIAL = "PrecisionAsisteAutomaDigitacionDetalleNoPresidencial.jrxml";
    public static final String PUESTA_CERO_STAE_REPORT_JRXML = "PuestaCeroStae.jrxml";
    public static final String SISTEMAS_AUTOMATIZADOS_JRXML = "SistemasAutomatizados.jrxml";
    public static final String LISTADO_PCS_JRXML = "ListadoPcs.jrxml";
    public static final String PROBABLES_CANDIDATOS_JRXML = "ProbablesCandidatosElectos.jrxml";
    public static final String REPORTE_AVANCE_DIGITALIZACION_LE_HA = "RPT06030103_avance_digitalizacion.jrxml";
    public static final String REPORTE_AVANCE_DIGITALIZACION_LE_DEN = "RPT06030103_avance_digitalizacion_den.jrxml";
    public static final String PRODUCTIVIDAD_DIGITADOR_JRXML = "ProductividadDigitador.jrxml";
    public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA = "ComparacionDigitacionAutomatica.jrxml";
    public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_9 = "ComparacionDigitacionAsistenteAutomatizadoPref_9.jrxml";
    public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_16 = "ComparacionDigitacionAsistenteAutomatizadoPref_16.jrxml";
    public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_32 = "ComparacionDigitacionAsistenteAutomatizadoPref_32.jrxml";
    public static final String BARRERA_ELECTORAL_PARLAMENTO_JRXML = "BarreraElectoral_Parlamento.jrxml";
    public static final String BARRERA_ELECTORAL_DIPUTADOS_JRXML = "BarreraElectoral_Diputados.jrxml";
    public static final String BARRERA_ELECTORAL_SENADORES_JRXML = "BarreraElectoral_Senadores.jrxml";
    public static final String REPORTE_RESUMEN_ESTADO_ACTAS = "ResumenEstadoActas.jrxml";
    public static final String REPORTE_DETALLE_AVANCE_MIEMBROS_MESA_ESCRUTINIO = "DetalleAvanceMiembrosMesaEscrutinio.jrxml";
    public static final String REPORTE_DETALLE_AVANCE_PERSONEROS = "DetalleAvancePersoneros.jrxml";

    public static final Integer ELECCION_PRESIDENCIAL = 10;
    public static final Integer ELECCION_REVOCATORIA = 7;
    public static final Integer ELECCION_SENADORES_DISTRITO_MULTIPLE = 14;
    public static final Integer ELECCION_SENADORES_DISTRITO_UNICO = 15;
    public static final Integer ELECCION_PARLAMENTO_ANDINO = 12;
    public static final Integer ELECCION_DIPUTADOS = 13;
    public static final Integer CANTIDAD_VOTOS_PREFERENCIALES_PARLAMENTO = 16;
    public static final Integer CANTIDAD_CURULES_PARLAMENTO_ANDINO = 16;
    public static final Integer ELECCION_CPR = 7;
    public static final Integer ELECCION_EMC = 4;

    public static final Integer SASA_RESPUESTA_ERROR = -1;
    public static final String SASA_TOKEN_NO_EXISTENTE = "SASA: Token No existente, vuelva a iniciar sesión";
    public static final String SASA_TOKEN_EXPIRADO_MENSAJE = "SASA: Token inválido, vuelva a iniciar sesión";
    public static final String SASA_SERVICIO_NO_DISPONIBLE = "SASA no disponible";
    public static final String SASA_ERROR_CAMBIO_CONSTRASENIA = "No se puede realizar el cambio de contraseña.";
    public static final String SASA_ERROR_ACTUALIZAR_DATOS = "Error al actualizar datos SASA";
    public static final Set<HttpStatusCode> SASA_TOKEN_INVALIDO_O_EXPIRADO_HTTP_STATUS = Set.of(HttpStatus.FORBIDDEN,
            HttpStatus.METHOD_NOT_ALLOWED);

    // CODIGO DE ELECCIONES
    public static final String COD_ELEC_PRE = "10";
    public static final String COD_ELEC_CONGRESALES = "11";
    public static final String COD_ELEC_PAR = "12";
    public static final String COD_ELEC_DIPUTADO = "13";
    public static final String COD_ELEC_SENADO_MULTIPLE = "14"; // SIMILAR A LA 10
    public static final String COD_ELEC_SENADO_UNICO = "15"; // SIMILAR A CONGRESO LIMA - HORIZONTAL
    public static final String COD_ELEC_REG = "01";
    public static final String COD_ELEC_CONSE = "02";
    public static final String COD_ELEC_PROV = "03";
    public static final String COD_ELEC_DIST = "04";
    public static final String COD_ELEC_REV_DIST = "07";
    public static final String COD_ELEC_REF_NACIONAL = "05";

    // CODIGO DE TIPOS DE REPORTE
    public static final Integer COD_TIPO_REPORTE_RESUMIDO = 1;
    public static final Integer COD_TIPO_REPORTE_DETALLADO = 2;

    public static final Integer ACTIVO = 1;
    public static final Integer INACTIVO = 0;

    public static final String CANDIDATOS_ORG_POL = "CandidatosOrgPol.jrxml";
    public static final String AUTORIDADES_EN_CONSULTA = "AutoridadesConsulta.jrxml";

    public static final String CODIGO_DISTRITO_ELECTORAL_NACION = "CODIGO_DISTRITO_NACION";

    public static final String SVO = "SIN VALOR OFICIAL";

    public static final String VACIO = "";

    public static final String DIPUTADOS = "13";

    public static final String FORMATO_FECHA = "dd-MM-yyyy HH:mm:ss";

    public static final Map<String, String> ESTADOS_ACTAS = Map.of(ESTADO_ACTA_PROCESADA, "procesada",
            ESTADO_ACTA_PROCESADA_RESOLUCION, "procesada");

    public static final String API_KEY_NACION = "nacion-api-key";

    public static final String TENANT_HEADER = "X-Tenant-Id";

    public static final String PATH_REPORT_TOTAL_CENTRO_COMPUTO_JRXML = "pe/gob/onpe/scebackend/reportes/totalCentroComputo/RPT040105.jrxml";

    public static final String CC_NACION_CODIGO = "C59000";
    public static final String CC_NACION_DESCRIPCION = "NACIÓN";
    public static final String DEFAULT_ESTADO_MESAS = "TODOS LOS ESTADOS";
    public static final String DIAS_SIN_VALOR_OFICIAL = "";

    public static final Long NCODI_AGRUPOL_VOTOS_BLANCOS = 80L;
    public static final Long NCODI_AGRUPOL_VOTOS_NULOS = 81L;
    public static final Long NCODI_AGRUPOL_VOTOS_IMPUGNADOS = 82L;

    public static final String NOMBRE_LOGO_ONPE = "onpe.jpg";
    public static final String REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE = "pixeltransparente.png";
    public static final String MENSAJE_ARCHIVO_NO_ENCONTRADO = "Archivo no encontrado: ";

    public static final String USUARIO_SYSTEM = "system";

    public static final String VERIFICA_VERSION_TITULO_REPORTE = "VERIFICACIÓN DE VERSIÓN SERVIDOR PRINCIPAL";
    public static final String REPORT_PARAM_NOMBRE_REPORTE = "nombre_reporte";

    public static final String VERIFICA_VERSION_JRXML = "VerificaVersion";
    public static final String PATH_REPORT_VERIFICA_VERSION_JRXML = "pe/gob/onpe/scebackend/reportes/verifica-version";
    public static final String EXTENSION_REPORTES_JASPER = ".jrxml";

    public static final String ESTADO_PENDIENTE = "P";
    public static final String ESTADO_APROBADO = "A";
    public static final String ESTADO_EJECUTADA = "E";
    public static final String ESTADO_RECHAZADO = "R";
    public static final String TIPO_AUTORIZACION_REPROCESAR_ACTA = "RA";

    public static final String TITULO_PRINCIPAL = "ELECCIONES GENERALES 2026";
    public static final String TITULO_ELECCION_VERIFICACION_DIGITACION_ACTA = "VERIFICACIÓN DE DIGITACIÓN DEL ACTA";
    public static final String TITULO_REPORTE_MESAS_ESTADO_MESA = "LISTADO DE MESAS POR ESTADO DE MESA";
    public static final String TITULO_REPORTE_MESAS_ESTADO_ACTA = "LISTADO DE MESAS POR ESTADO DE ACTA";
    public static final String TITULO_REPORTE_MESAS_ESTADO_DIGITACION = "LISTADO DE MESAS POR ESTADO DE DIGITACIÓN";
    public static final String TITULO_REPORTE_INFORMACION_OFICIAL = "MONITOREO DE INFORMACIÓN OFICIAL";
    public static final String TITULO_REPORTE_PROCEDE_PAGO = "PROCEDE PAGO DE MIEMBROS DE MESA SEGÚN ACTA DE ESCRUTINIO";
    public static final String TITULO_REPORTE_COMPARACION_OMISOS_AUSENTISMO = "COMPARACIÓN OMISOS VS AUSENTISMO";

    public static final Integer LOG_TRANSACCIONES_AUTORIZACION_SI = 1;
    public static final Integer LOG_TRANSACCIONES_AUTORIZACION_NO = 0;
    public static final Integer LOG_TRANSACCIONES_ACCION = 1;
    public static final String LOG_TRANSACCIONES_TIPO_REPORTE = "reporte";
    public static final Integer LENGTH_BEARER = 7;

    public static final String CONTENT_TYPE = "Content-Type";

    public static final double PORCENTAJE_COMPLETO_ACTAS_CONTABILIZADAS = 100.000;

    public static final String REPORT_PARAM_TITULO = "titulo";
    public static final String REPORT_PARAM_TITULO_REPORTE = "titulo_reporte";
    public static final String REPORT_PARAM_PIXEL_TRANSPARENTE = "pixeltransparente";
    public static final String REPORT_PARAM_DESC_CC = "desCComp";
    public static final String REPORT_PARAM_DESC_ODPE = "desOdpe";
    public static final String REPORT_PARAM_VERSION = "version";
    public static final String REPORT_PARAM_USUARIO = "usuario";
    public static final String REPORT_PARAM_SERVIDOR = "servidor";
    public static final String REPORT_PARAM_ESTACION = "estacion";
    public static final String REPORT_PARAM_SIN_VALOR_OFICIAL = "sinvaloroficial";
    public static final String REPORT_PARAM_URL_IMAGE = "url_imagen";
    public static final String REPORT_PARAM_CODIGO_VERSION_NACION = "codigo_version_nacion";
    public static final String REPORT_PARAM_FECHA_VERSION_NACION = "fecha_auditoria_nacion";
    public static final String REPORT_PARAM_FECHA_CARGA_BDONPE = "fechaCargaBdonpe";
    public static final String REPORT_PARAM_IMAGEN_ONPE = "onpe.jpg";

    // Util
    public static final Integer DIAS_PREVIOS_MARCA_DE_AGUA = -2;

    public static final Integer ETAPA_SIN_CARGA = 0;
    public static final Integer ETAPA_CON_CARGA = 1;
    public static final String NAME_SP_VISTA = "vista";
    public static final String NAME_SP_CIUDADANO = "ciudadana";

    public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA = "AC";
    public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_RESOLUCION = "RE";
    public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_LISTA_ELECTORES = "LE";
    public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_HOJA_ASISTENCIA = "HA";
    public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_DENUNCIAS = "DE";

    public static final String MENSAJE_SOLICITUD_USUARIO = "Solicitud del usuario ";

    public static final String PREFIJO_UBIGEO_EXTRANJEROS = "9";

    public static final String DESC_ACTA_ASOCIADA_RESOLUCION = "ACTA ASOCIADA A RESOLUCIÓN";
    public static final String DESC_ACTA_RECIBIDA = "ACTA RECIBIDA";
    public static final String MSG_TRAZABILIDAD_PROCESO_ACTA_APROBADA_CTRL_DIGTAL = "ACTA APROBADA EN CONTROL DE DIGITALIZACIÓN";
    public static final String MSG_TRAZABILIDAD_PROCESO_ACTA_RECHAZADA_CTRL_DIGTAL = "ACTA RECHAZADA EN CONTROL DE DIGITALIZACIÓN";

    // CATALOGOS
    public static final String CATALOGO_MAE_TIPO_AUTORIZACION = "mae_tipo_autorizacion";
    public static final String CATALOGO_DET_TIPO_AUTORIZACION = "c_tipo_autorizacion";
    public static final String CATALOGO_MAE_ESTADO_APROBACION_AUTORIZACION = "mae_estado_aprobacion_autorizacion";
    public static final String CATALOGO_DET_ESTADO_APROBACION = "c_estado_aprobacion";
    public static final String CATALOGO_MAE_TIPO_DOCUMENTO = "mae_tipo_documento";
    public static final String CATALOGO_DET_TIPO_DOCUMENTO = "c_tipo_documento";

    public static final String TXT_OMISOS_VOTANTES = "VOTANTES";
    public static final String TXT_OMISOS_MIEMBROS_MESA = "MIEMBROS DE MESA";
    public static final String TXT_PERSONEROS = "PERSONEROS";
    public static final String TXT_MIEMBRO_MESA_ESCRUTINIO = "MIEMBROS DE MESA ESCRUTINIO";

    public static final String ACCESO_PC_TIPO_DOCUMENTO_ID_PC = "IDPC";

    public static final String NOMBRE_NACION_DISTRITO_ELECTORAL = "NACION";

    public static final Integer N_ACHURADO = 20;
    public static final Integer N_PARTICIPA = 1;
    public static final String SEPARADOR_ERRORES = ",";
    public static final Set<String> CODIGOS_ELECCIONES_PREFERENCIALES = Set.of(ConstantesComunes.COD_ELEC_DIPUTADO,
            ConstantesComunes.COD_ELEC_PAR, ConstantesComunes.COD_ELEC_SENADO_UNICO,
            ConstantesComunes.COD_ELEC_SENADO_MULTIPLE);

    public static final String ABREV_ACTA_ESCRUTINIO_EXTRANJERO = "AEE";
    public static final String ABREV_ACTA_ESCRUTINIO_EXTRANJERO_HORIZONTAL = "AEEH";
    public static final String ABREV_ACTA_INSTALACION_SUGRAFIO_EXTRANJERO = "AISE";

    public static final Long ID_DOCUMENTO_ELECTORAL_AE = 2L;
    public static final Long ID_DOCUMENTO_ELECTORAL_AIS = 3L;

    public static final String TEXT_TRUE = "true";
    public static final String TEXT_FALSE = "false";
}
