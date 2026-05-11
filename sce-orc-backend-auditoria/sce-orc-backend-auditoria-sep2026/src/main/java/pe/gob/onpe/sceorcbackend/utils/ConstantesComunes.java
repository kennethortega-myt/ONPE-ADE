package pe.gob.onpe.sceorcbackend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ConstantesComunes {

  public static final String COMPARAR_DIGITACION_CASO_1 = "1";
  public static final String COMPARAR_DIGITACION_CASO_2 = "2";

  public static final String USER_MODELO = "MODELS_CC_USER";

  public static final String TEXT_ANULADA = "anulada";
  public static final String TEXT_EXTRAVIADA = "EXTRAVIADA";
  public static final String TEXT_SINIESTRADA = "SINIESTRADA";
  public static final String TEXT_ENCONTRADA = "emcontrada";
  public static final String TEXT_RECONTEO = "reconteo";
  public static final String ORIGINAL_ACCESS_DTO = "javax.xml.accessExternalDTD";
  public static final String ORIGINAL_ACCESS_SCHEMA = "javax.xml.accessExternalSchema";
  public static final String NOMBRE_SISTEMA = "Sistema de Computo Electoral";
  public static final String ABREV_NOMBRE_SISTEMA = "SCE";

  public static final String USER_TOKEN_PREFIX = "user:";
  public static final String TOKEN_SUFFIX = ":token";
  public static final String REFRESH_TOKEN_SUFFIX = ":refresh-token";
  public static final String BLACKLISTED_VALUE = "blacklisted";

  public static final String DOS_PUNTOS = ":";

  public static final Pattern SAFE_FILENAME = Pattern.compile("^[a-zA-Z0-9._\\-()\\s]+$");

  public static final Integer TIEMPO_INACTIVIDAD_MINUTOS = 20;

  public static final String ALGORITHM_SHA_256 = "SHA-256";

  public static final String DESC_ACTA_ASOCIADA_RESOLUCION = "ACTA ASOCIADA A RESOLUCIÓN";
  public static final String DESC_ACTA_RECIBIDA = "ACTA RECIBIDA";


  public static final int CODEBAR_MIN_LENGTH = 9;
  public static final int CODEBAR_MAX_LENGTH = 15;

  public static final int EXPECTED_FILE_COUNT_MM = 3;

  public static final String VALIDACION_TIF_01_MM = "TIF 01";
  public static final String VALIDACION_TIF_02_MM = "TIF 02";


  public static final Pattern CODEBAR_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])[A-Z\\d]+$");
  public static final Pattern NRO_MESA_PATTERN = Pattern.compile("^\\d+$");
  public static final Pattern NUMERO_RESOLUCION_PATTERN =
          Pattern.compile("^[A-Za-z0-9./\\-]+(?: [A-Za-z0-9./\\-]+){0,49}$");
  public static final long MAX_SIZE_ACTA = 5L * 1024 * 1024; //10 megas
  public static final long MAX_SIZE_RESOLUCION = 10L * 1024 * 1024;
  public static final long MAX_SIZE_DENUNCIAS = 10L * 1024 * 1024;
  public static final long MAX_SIZE_LISTA_ELECTORES = 50L * 1024 * 1024;
  public static final long MAX_SIZE_HOJA_ASISTENCIA = 10L * 1024 * 1024;



  private ConstantesComunes() {

    }

    public static final String TEXTO_COMPLETA = "COMPLETA";
    public static final String TEXTO_VALIDADO_AUTOMATICO = "Validado automaticamente";
    public static final String TEXTO_OPERACION_EXITOSA = "Operación realizada con éxito";
    public static final String NO_SE_ENCONTRARON_REGISTROS = "No se encontraron registros.";

    public static final String PERFIL_USUARIO_VERIFICADOR = "VERI";
    public static final String PERFIL_USUARIO_CONTROL_DIGITALIZACION = "CON";
    public static final String PERFIL_USUARIO_SUPER_ADMINISTRADOR = "SUP_ADM";
    public static final String PERFIL_USUARIO_ADMINISTRADOR_CC = "ADM_CC";
    public static final String PERFIL_USUARIO_ADMINISTRADOR_NAC = "ADM_NAC";
    public static final String PERFIL_USUARIO_SCE_SCANNER = "PER_DIGI";
    public static final String PERFIL_USUARIO_REPORTES = "REPO";


    public static final String CVALUE_NULL = null;
    public static final Long NVALUE_NULL = null;

    public static final String  DIPUTADOS  = "13";

    public static final String PKG_RM = "";//vacio para las PCP y _RM para las regionales y municipales
    public static final String NOMBRE_SERVIDOR_BD = "09b078460553";

    public static final int TIPO_PROCESO = 1;

    public static final int PROCESO_R2V = 4; //Elecciones regionales 2da vuelta

    public static final String PROCESO_SEP_ABREV = "SEP";
    public static final String PROCESO_ERM_ABREV = "ERM";
    public static final String PROCESO_EMC_ABREV = "EMC";
    public static final String PROCESO_CPR_ABREV = "CPR";
    public static final String PROCESO_EG_ABREV = "EG";
    public static final String PROCESO_PRI_ABREV = "PRI";

    public static final String SVO = "SIN VALOR OFICIAL";
    
    //CODIGO DE ELECCIONES
    public static final String COD_ELEC_PRE = "10";
    public static final String COD_ELEC_CONGRESALES = "11";
    public static final String COD_ELEC_PAR = "12";
    public static final String COD_ELEC_DIPUTADO = "13";
    public static final String COD_ELEC_SENADO_MULTIPLE = "14";
    public static final String COD_ELEC_SENADO_UNICO = "15";     //SIMILAR A CONGRESO LIMA - HORIZONTAL
    public static final String COD_ELEC_REG = "01";
    public static final String COD_ELEC_CONSE = "02";
    public static final String COD_ELEC_PROV = "03";
    public static final String COD_ELEC_DIST = "04";
    public static final String COD_ELEC_REV_DIST = "07";
    public static final String COD_ELEC_REF_NACIONAL = "05";


  public static final Set<String> CODIGOS_ELECCIONES_PREFERENCIALES = Set.of(
      ConstantesComunes.COD_ELEC_DIPUTADO,
      ConstantesComunes.COD_ELEC_PAR,
      ConstantesComunes.COD_ELEC_SENADO_UNICO,
      ConstantesComunes.COD_ELEC_SENADO_MULTIPLE
  );

    //CODIGO DE TIPOS DE REPORTE
    public static final Integer COD_TIPO_REPORTE_RESUMIDO = 1;
    public static final Integer COD_TIPO_REPORTE_DETALLADO = 2;

    public static final String COD_PROCESO_REGIONAL = "01";
    public static final String COD_PROCESO_MUNICIPAL = "02";
    public static final String NOMBL_ELEC_REG = "ELECCION REGIONAL";
    public static final String NOMBL_ELEC_PROV = "ELECCION MUNICIPAL";

    public static final Long ID_DOCUMENTO_ELECTORAL_AE = 2L;
    public static final Long ID_DOCUMENTO_ELECTORAL_AIS = 3L;

    public static final Long NCODI_AGRUPOL_VOTOS_BLANCOS = 80L;
    public static final Long NCODI_AGRUPOL_VOTOS_NULOS = 81L;
    public static final Long NCODI_AGRUPOL_VOTOS_IMPUGNADOS = 82L;
    public static final String DESC_AGRUPOL_VOTOS_BLANCOS = "VOTOS BLANCOS";
    public static final String DESC_AGRUPOL_VOTOS_NULOS = "VOTOS NULOS";
    public static final String DESC_AGRUPOL_VOTOS_IMPUGNADOS = "VOTOS IMPUGNADOS";




    public static final Integer N_POSICION_CPR_VOTOS_SI = 1;
    public static final Integer N_POSICION_CPR_VOTOS_NO = 2;

    public static final Integer N_POSICION_CPR_VOTOS_BLANCOS = 3;

    public static final Integer N_POSICION_CPR_VOTOS_NULOS = 4;

    public static final Integer N_POSICION_CPR_VOTOS_IMPUGNADOS = 5;


    public static final String C_VALUE_ILEGIBLE = "#";
    public static final String VALUE_CVAS_INCOMPLETA = "N";

    public static final String VALUE_SI = "SI";
    public static final String VALUE_NO = "NO";

    public static final String TEXTO_NULL = "null";

    public static final String VACIO = "";

    public static final String SEPARADOR_ERRORES = ",";

    public static final Long NVALUE_ZERO = 0L;

    public static final String CVALUE_ZERO = "0";
    public static final Long NVALUE_UNO = 1L;


    public static final int POSICION_COLUMNA_REV_VOTOS_SI         = 1;
    public static final int POSICION_COLUMNA_REV_VOTOS_NO         = 2;
    public static final int POSICION_COLUMNA_REV_VOTOS_BLANCOS    = 3;
    public static final int POSICION_COLUMNA_REV_VOTOS_NULOS      = 4;
    public static final int POSICION_COLUMNA_REV_VOTOS_IMPUGNADOS = 5;

    public static final int POSICION_COLUMNA_REV_VOTOS_TOTAL = 6;



    private static final Map<Long, String> MAP_NAME_POSICION_REVOCATORIA = new HashMap<>();

    static {
        MAP_NAME_POSICION_REVOCATORIA.put((long) POSICION_COLUMNA_REV_VOTOS_SI, "SI");
        MAP_NAME_POSICION_REVOCATORIA.put((long) POSICION_COLUMNA_REV_VOTOS_NO, "NO");
        MAP_NAME_POSICION_REVOCATORIA.put(NCODI_AGRUPOL_VOTOS_BLANCOS, "Blanco");
        MAP_NAME_POSICION_REVOCATORIA.put(NCODI_AGRUPOL_VOTOS_NULOS, "Nulos");
        MAP_NAME_POSICION_REVOCATORIA.put(NCODI_AGRUPOL_VOTOS_IMPUGNADOS, "Impugnados");
    }

    public static Map<Long, String> getMapNamePosicionRevocatoria() {
        return MAP_NAME_POSICION_REVOCATORIA;
    }



    //CARGO
    public static final int POSICION_CARGO_PRESIDENTE        = 1;
    public static final int POSICION_CARGO_SECRETARIO        = 2;
    public static final int POSICION_CARGO_TERCER_MIEMBRO    = 3;
    public static final int POSICION_CARGO_PRIMER_SUPLENTE      = 4;
    public static final int POSICION_CARGO_SEGUNDO_SUPLENTE = 5;
    public static final int POSICION_CARGO_TERCER_SUPLENTE = 6;



    private static final Map<Integer, String> MAP_NAME_CARGOS_MIEMBROS_MESA = new HashMap<>();

    static {
        MAP_NAME_CARGOS_MIEMBROS_MESA.put(POSICION_CARGO_PRESIDENTE, "Presidente");
        MAP_NAME_CARGOS_MIEMBROS_MESA.put(POSICION_CARGO_SECRETARIO, "Secretario");
        MAP_NAME_CARGOS_MIEMBROS_MESA.put(POSICION_CARGO_TERCER_MIEMBRO, "Tercer Miembro");
        MAP_NAME_CARGOS_MIEMBROS_MESA.put(POSICION_CARGO_PRIMER_SUPLENTE, "Primer Suplente");
        MAP_NAME_CARGOS_MIEMBROS_MESA.put(POSICION_CARGO_SEGUNDO_SUPLENTE, "Segundo Suplente");
        MAP_NAME_CARGOS_MIEMBROS_MESA.put(POSICION_CARGO_TERCER_SUPLENTE, "Tercer Suplente");
    }

    public static Map<Integer, String> getMapNameCargosMiembrosMesa() {
        return MAP_NAME_CARGOS_MIEMBROS_MESA;
    }



    public static final String TEXTO_ACTA_SIN_FIRMAS = "ACTA SIN FIRMAS";
    public static final String TEXTO_ACTA_SIN_DATOS = "ACTA SIN DATOS";
    public static final String TEXTO_ACTA_INCOMPLETA = "ACTA INCOMPLETA";
    public static final String TEXTO_SOLICITUD_DE_NULIDAD = "SOLICITUD DE NULIDAD";


    public static final String PATH_IMAGE_COMMON = "pe/gob/onpe/sceorcbackend/common/image/";
  public static final String PATH_IMAGE_COMMON_NAC = "pe/gob/onpe/sceorcbackend/common/image/";

    public static final String EXTENSION_REPORTES_JASPER = ".jrxml";
    //RESOLUCIONES
    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA = "CargoEntActasEnviadasJee";
    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_REV = "CargoEntActasEnviadasJeeRev";
    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_ACTA_DEVUELTA = "CargoEntActasDevueltas";
    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_MESA_NO_INSTALADA = "CargoEntMesasNoInstaladas";
    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_EXT_SINIE = "CargoEntExtravidasSiniestradas";

    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_INFUNDADA = "CargoEntResolucionesInfundadas";
    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_INFUNDADA_XUBIGEO = "CargoEntResolucionesInfundadasUbigeo";
    public static final String RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_ANULADAS_X_UBIGEO = "CargoEntResolucionesAnuladasxUbigeo";
    public static final String AVANCE_MESA_REPORT_JRXML = "AvanceMesa.jrxml";
    public static final String RESUMEN_TOTAL_POR_CC_REPORT_JRXML = "ResumenTotalPorCC.jrxml";

    public static final String CONTABILIZACION_VOTOS_REPORT_JRXML = "ContabilizacionVotosMesa.jrxml";
    public static final String AVANCE_ESTADO_ACTAS_REPORT_JRXML = "AvanceEstadoActas.jrxml";
    public static final String MESAS_A_INSTALAR_SUBREPORT_JRXML = "SubReporteMesasAInstalar.jrxml";
    public static final String MESAS_A_INSTALAR_SUBREPORT_JASPER = "SubReporteMesasAInstalar.jasper";
    
    public static final String OFICIO_ACTAS_OBSERVADAS_JRXML = "OficioActasObservadas";

    public static final String PATH_REPORT_JRXML = "pe/gob/onpe/sceorcbackend/reportes";
    public static final String VERIFICA_VERSION_JRXML = "VerificaVersion";
    public static final String PUESTA_CERO_TITULO_REPORTE = "Reporte de Puesta a Cero";

    public static final String VERIFICA_VERSION_TITULO_REPORTE = "VERIFICACIÓN DE VERSIÓN SERVIDOR PRINCIPAL";
    public static final String PUESTA_CERO_REPORT_JRXML = "PuestaCeroCc";


    //DIGITOS
    public static final String OPTION_1ER_CONTROL_CALIDAD_RESOLUCION = "1CCRESOL";
    public static final String OPTION_ASOCIAR_RESOLUCION = "ASOCIAR_RESOL";
    public static final String OPTION_APLICAR_RESOLUCION = "APLICAR_RESOL";

    public static final Integer ACTIVO = 1;
    public static final Integer INACTIVO = 0;


    public static final String ABREV_DOCUMENT_LISTA_ELECTORES = "LE";
    public static final String ABREV_DOCUMENT_HOJA_DE_ASISTENCIA = "HA";//HOJA DE ASISTENCIA DE MIEMBROS DE MESA
    public static final String COD_DOCUMENT_MIEMBROS_DE_MESA = "01";

    public static final String ABREV_DOCUMENT_HOJA_DE_CONTROL_DE_ASISTENCIA_MIEMBROS_MESA = "MM";

    public static final String ABREV_DOCUMENT_MIEMBROS_DE_MESA_NO_SORTEADOS = "MMC";
    public static final String COD_DOCUMENT_MIEMBROS_DE_MESA_NO_SORTEADOS = "02";


    public static final Integer LONGITUD_CADENA_LE = 17;
    public static final Integer LONGITUD_CADENA_MM = 13;

    public static final Integer N_ACHURADO = 20;
    public static final Integer N_PARTICIPA = 1;


    public static final String ESTADO_PENDIENTE = "P";
    public static final String ESTADO_APROBADO = "A";
    public static final String ESTADO_EJECUTADA = "E";
    public static final String ESTADO_RECHAZADO = "R";


    private static final Map<String, String> MAP_ESTADOS_COMUNES = new HashMap<>();

    static {
        MAP_ESTADOS_COMUNES.put(ESTADO_PENDIENTE, "Pendiente");
        MAP_ESTADOS_COMUNES.put(ESTADO_EJECUTADA, "Ejecutado");
        MAP_ESTADOS_COMUNES.put(ESTADO_APROBADO, "Aprobado");
        MAP_ESTADOS_COMUNES.put(ESTADO_RECHAZADO, "Rechazado");
    }

    public static Map<String, String> getMapEstadosComunes() {
        return MAP_ESTADOS_COMUNES;
    }


    public static final Integer N_DISTRIBUCION_RESOLUCIONES_ASOCIAR = 2;//50
    public static final Integer N_DISTRIBUCION_ACTAS_VERIFICACION = 20;//50
    public static final Integer N_DISTRIBUCION_CONTROL_DIGTAL_DENUNCIAS = 20;
    public static final Integer N_DISTRIBUCION_CONTROL_DIGTAL_RESOLUCIONES = 20;
    public static final Integer N_DISTRIBUCION_LE_VERIFICACION = 20;

    public static final Integer N_DISTRIBUCION_ACTAS_POR_CORREGIR = 5;

    public static final Integer N_DISTRIBUCION_ACTAS_PROCESAMIENTO_MANUAL = 10;

    public static final String ABREV_ACTA_ESCRUTINIO = "AE";
    public static final String ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL = "AESH";
    public static final String ABREV_ACTA_ESCRUTINIO_HORIZONTAL = "AEH";
    public static final String ABREV_ACTA_ESCRUTINIO_STAE = "AES";
    public static final String ABREV_ACTA_ESCRUTINIO_EXTRANJERO_PADRE = "EXT";
    public static final String ABREV_ACTA_ESCRUTINIO_EXTRANJERO = "AEE";
    public static final String ABREV_ACTA_ESCRUTINIO_EXTRANJERO_HORIZONTAL = "AEEH";

    public static final String ABREV_ACTA_INSTALACION_SUGRAFIO = "AIS";

    public static final String ABREV_ACTA_INSTALACION_STAE   = "AIE";
    public static final String ABREV_ACTA_SUFRAGIO_STAE = "ASE";

    public static final String ABREV_ACTA_INSTALACION_SUGRAFIO_EXTRANJERO = "AISE";



  public static final List<String> LISTA_ABREV_ACTA_ESCRUTINIO = List.of(
      ConstantesComunes.ABREV_ACTA_ESCRUTINIO,
      ConstantesComunes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL,
      ConstantesComunes.ABREV_ACTA_ESCRUTINIO_STAE,
      ConstantesComunes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,
      ConstantesComunes.ABREV_ACTA_ESCRUTINIO_EXTRANJERO,
      ConstantesComunes.ABREV_ACTA_ESCRUTINIO_EXTRANJERO_HORIZONTAL
  );

  public static final List<String> LISTA_ABREV_ACTA_INSTALACION_SUFRAGIO = List.of(
          ConstantesComunes.ABREV_ACTA_INSTALACION_SUGRAFIO,
          ConstantesComunes.ABREV_ACTA_INSTALACION_SUGRAFIO_EXTRANJERO,
          ConstantesComunes.ABREV_ACTA_INSTALACION_STAE,
          ConstantesComunes.ABREV_ACTA_SUFRAGIO_STAE
  );

    public static final String GUION_MEDIO = "-";
    public static final String GUION_BAJO = "_";

    public static final String TIPO_AUTORIZACION_PUESTA_CERO = "PC";
    public static final String TIPO_AUTORIZACION_REPROCESAR_ACTA = "RA";
    public static final String TIPO_AUTORIZACION_VER_AGRUPACION_POLITICA = "VAP";
    public static final String TIPO_AUTORIZACION_REAPERTURA_CC = "RCC";

    public static final String MENSAJE_SOLICITUD_USUARIO = "Solicitud del usuario ";

    public static final Integer CANTIDAD_COLUMNAS_REVOCATORIA = 5;//votos SI, NO , Blancos, Nulos, Impuganados
    public static final Long SOLUCION_TECNOLOGICA_CONVENCIONAL = 3L;

    public static final Long PROC_MANUAL_DIGITALIZACION_ESCRUTINIO = 3L;
    public static final Long PROC_MANUAL_DIGITALIZACION_SUF_INST = 3L;

    // Estados de digitalización (n_digitalizacion)
    public static final Long N_DIGITALIZACION_REDIGITALIZAR = 0L;
    public static final Long N_DIGITALIZACION_VALIDADO_AUTOMATICAMENTE = 1L;
    public static final Long N_DIGITALIZACION_REDIGITALIZAR_2 = 2L;
    public static final Long N_DIGITALIZACION_PROCESAMIENTO_MANUAL = 3L;
    public static final Long N_DIGITALIZACION_REDIGITALIZAR_4 = 4L;

    // Textos de estados de digitalización
    public static final String TEXT_DIGITALIZACION_REDIGITALIZAR = "Acta para redigitalizar";
    public static final String TEXT_DIGITALIZACION_VALIDADO_AUTOMATICAMENTE = "Acta validada automáticamente por ONPE";
    public static final String TEXT_DIGITALIZACION_PROCESAMIENTO_MANUAL = "Acta para procesamiento manual";

    public static final String SOLUCION_TECNOLOGICA_TEXT_CONVENCIONAL = "CONVENCIONAL";
    public static final String SOLUCION_TECNOLOGICA_TEXT_STAE = "STAE";

    public static final Integer TIPO_HOJA_STAE_TRANSMITIDA = 1;
    public static final Integer TIPO_HOJA_STAE_NO_TRANSMITIDA = 2;
    public static final Integer TIPO_HOJA_STAE_CONTINGENCIA = 3;
    public static final Integer TIPO_HOJA_CONVENCIOANL = 4;
    public static final String DEFAULT_DESCRIPCION = "Desconocido";


    private static final Map<Integer, String> TIPO_HOJA_MAP = new HashMap<>();
    static {
        TIPO_HOJA_MAP.put(TIPO_HOJA_STAE_TRANSMITIDA, TIPO_HOJA_STAE_TRANSMITIDA + "- STAE Transmitida");
        TIPO_HOJA_MAP.put(TIPO_HOJA_STAE_NO_TRANSMITIDA, TIPO_HOJA_STAE_NO_TRANSMITIDA+ "- STAE No Transmitida");
        TIPO_HOJA_MAP.put(TIPO_HOJA_STAE_CONTINGENCIA, TIPO_HOJA_STAE_CONTINGENCIA+ "- STAE Contingencia");
        TIPO_HOJA_MAP.put(TIPO_HOJA_CONVENCIOANL, TIPO_HOJA_CONVENCIOANL+ "- Convencional");
    }


    public static String getDescripcionStae(Integer tipoHoja) {
        return TIPO_HOJA_MAP.getOrDefault(tipoHoja, DEFAULT_DESCRIPCION);
    }

    public static final Long SOLUCION_TECNOLOGICA_STAE = 2L;
    public static final Long SOLUCION_TECNOLOGICA_VOTO_DIGITAL = 4L;
    public static final Long SOLUCION_TECNOLOGICA_VEP = 1L;

    public static final Integer INDEX_ZERO = 0;

    public static final Long N_ARCHIVO_ILEGIBLE = -1L;


    public static final String TEXT_TRUE = "true";
    public static final String TEXT_FALSE = "false";

    public static final String DET_ACTA_ACCION_TIEMPO_INI = "INICIO";
    public static final String DET_ACTA_ACCION_TIEMPO_FIN = "FIN";

    public static final String DET_ACTA_ACCION_PROCESO_RECIBIDA = "RECIBIDA";
    public static final String USUARIO_CONSULADO = "PER_DIGI_C_";
    public static final String DET_ACTA_ACCION_PROCESO_CONTROL_DIGTAL = "CONTROL-DIGITALIZACION";
    public static final String DET_ACTA_ACCION_PROCESO_MODELO_PROCESAR = "MODELO_PROCESAR";
    public static final String DET_ACTA_ACCION_PROCESO_1ERA_VERI = "1ra DIGITACIÓN";
    public static final String DET_ACTA_ACCION_PROCESO_2DA_VERI = "2da DIGITACIÓN";
    public static final String DET_ACTA_ACCION_PROCESO_POR_CORREGIR = "ACTA-POR-CORREGIR";
    public static final String DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD = "CONTROL-CALIDAD";
    public static final String DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD_RECHAZAR = "CONTROL-CALIDAD-RECHAZAR";
    public static final String DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD_OBSERVAR = "CONTROL-CALIDAD-OBSERVAR";

    public static final String NONE = "NONE";

    public static final String MSG_TRAZABILIDAD_PROCESO_ACTA_APROBADA_CTRL_DIGTAL = "ACTA APROBADA EN CONTROL DE DIGITALIZACIÓN";
    public static final String MSG_TRAZABILIDAD_PROCESO_ACTA_RECHAZADA_CTRL_DIGTAL = "ACTA RECHAZADA EN CONTROL DE DIGITALIZACIÓN";

    public static final String MSJ_ERROR = "Error:";
    public static final String MSJ_LOG_ATERISCOS = "****************************";


    public static final String COLUMNA_ESTADO_ACTA = "cEstadoActa";

    public static final String COLUMNA_DET_ACTA_ID = "detActa.id";

    public static final String COLUMNA_CAB_ACTA_ID = "cabActa.id";

    public static final String COLUMNA_ACTA_ID = "actaId";

    public static final String COLUMNA_SECCION = "seccion";

    public static final String COLUMNA_C_PROVINCIA = "cProvincia";

    public static final String COLUMNA_ID_UBIGEO = "idUbigeo";

    public static final String COLUMNA_UBIGEO = "ubigeo";

    public static final String COLUMNA_AMBITO_ELECTORAL_ID = "ambitoElectoral.id";

    public static final String COLUMNA_CENTRO_COMPUTO_ID = "centroComputo.id";

    public static final String COLUMNA_C_DEPARTAMENTO = "cDepartamento";

    public static final String COLUMNA_ELECCION = "eleccion";

    public static final String COLUMNA_ELECCION_ID = "eleccion.id";

    public static final String URL_NACION_RECIBIR_AUTORIZACION ="autorizacion/recibir-autorizacion";

    public static final String URL_NACION_RECIBIR_SOLICITUD_AUTORIZACION ="autorizacion/crear-solicitud-autorizacion";


    public static final String MENSAJE_FORMAT_MESA_NO_EXISTE = "La mesa %s no existe.";
    public static final String MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE ="La mesa %s ha sido validada correctamente.";

    public static final String MENSAJE_FORMATO_MESA_CON_6_CARACTERES = "La mesa %s debe tener 6 caracteres.";

    public static final String MENSAJE_FORMATO_MESA_ESTADO_NO_INSTALADA = "La mesa %s se encuentra en estado NO INSTALADA.";

    public static final String MENSAJE_FORMATO_ACTA_ESTADO_NO_DEVUELTA = "El acta %s-%s no se encuentra en estado devuelta.";
    public static final String MENSAJE_FORMATO_ACTA_ESTADO_PARA_ENVIO_JURADO = "El acta %s-%s está para envío al JEE/JNE.";

    public static final String MENSAJE_LOG_ERROR_ACTA_NO_EXISTE_PARA_ELECION= "El acta {}, no se encuentra registrado.";

  public static final String MENSAJE_FORMATO_ACTA_ID_NO_EXISTE = "El acta %s no existe para la elección %s.";

    public static final String MENSAJE_FORMATO_COPIA_RANGO_NO_CONFIGURADO= "La copia %s no se encuentra en el rango configurado [%s-%s].";

    public static final String MENSAJE_FORMAT_MESA_NO_REGISTRADA_EN_BD = "La mesa %s, no está registrada en la Base de Datos.";

    public  static final String TEXT_HORA_CERO ="00:00";

  public  static final String OFICINA_DEFAULT ="LIMA";

  public static final String SALTO_LINEA = "<br/>";
  public static final String ESPACIO_DOBLE ="&nbsp;&nbsp;";
  public static final String HTML_INICIO_UL = "</u>";
  public static final String HTML_FIN_UL = "</u>";
  public static final String HTML_INICIO_LI = "</li>";
  public static final String HTML_FIN_LI = "</li>";

  public  static final String MOTIVO_DEFAULT ="Fin de actividades del centro de cómputo";

    public static final String  MENSAJE_LOGGER_ERROR = "Error: {}";

    public static final String  MENSAJE_LOGGER_ERROR_STACK = "Error:";

    public static final String MENSAJE_LOGGER_FIN_TRANSMISION = "Fin de la transmision {}.";

    public static final String MENSAJE_LOGGER_CANTIDAD_REGISTROS_TRANSMISION = "Cantidad de registros para la transmision: {}.";

    public static final String  REPORT_PARAM_TITULO = "titulo";
    public static final String  REPORT_PARAM_TITULO_REPORTE = "titulo_reporte";

    public static final String  REPORT_PARAM_SERVIDOR = "servidor";

    public static final String REPORT_PARAM_CORRELATIVO = "correlativo";
    public static final String  REPORT_PARAM_DESC_CC = "desCComp";

    public static final String  REPORT_PARAM_FECHA_LOTI = "fechaLoti";
    public static final String  REPORT_PARAM_DESC_ODPE = "desOdpe";

    public static final String  REPORT_PARAM_USUARIO = "usuario";

    public static final String REPORT_PARAM_CODIGO_VERSION_NACION = "codigo_version_nacion";
    public static final String REPORT_PARAM_FECHA_VERSION_NACION ="fecha_auditoria_nacion";
    public static final String REPORT_PARAM_FECHA_CARGA_BDONPE ="fechaCargaBdonpe";

    public static final String REPORT_PARAM_MODELO_HASH_NACION = "modelo_hash_nacion";
    public static final String REPORT_PARAM_MODELO_NRO = "modelo_nro";
    public static final String REPORT_PARAM_MODELO_HASH = "modelo_hash";
    public static final String REPORT_PARAM_MODELO_ESTADO = "modelo_estado";

    public static final String ESTADO_COMPARACION_IGUALES = "IGUALES";
    public static final String ESTADO_COMPARACION_DIFERENTES = "DIFERENTES";
    public static final String ESTADO_COMPARACION_NO_DISPONIBLE = "NO DISPONIBLE";

    public static final String  REPORT_PARAM_FECHA_RESOLUCION = "fechaResolucion";

    public static final String  REPORT_PARAM_CODIGO_RESOLUCION = "codigoResolucion";


    public static final String  REPORT_PARAM_NOMBRE_REPORTE = "nombre_reporte";


    public static final String  REPORT_PARAM_PIXEL_TRANSPARENTE = "pixeltransparente";

    public static final String  REPORT_PARAM_VERSION = "version";

    public static final String  REPORT_PARAM_SIN_VALOR_OFICIAL = "sinvaloroficial";

    public static final String  REPORT_PARAM_URL_IMAGE = "url_imagen";

    public static final String REPORT_PARAM_IMAGEN_ONPE = "onpe.jpg";

    public static final String REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE = "pixeltransparente.png";

    public static final String  REPORT_PARAM_SUBREPORT_DIR = "SUBREPORT_DIR";
    public static final String  REPORT_PARAM_LISTA_ERROR_MATERIAL = "listaErrorMaterial";

    public static final String CAT_ESTRUCTURA_COLUMN_MAESTRO_TIPO_FORMATO = "mae_tipo_formato";


    public static final String MENSAJE_FILE_NOT_FOUND = "File not found";

    public static final String MENSAJE_TOKEN_INVALIDO = "Token Inválido";

    public static final String MENSAJE_ARCHIVO_NO_ENCONTRADO = "Archivo no encontrado: ";




    public static final String BD_COLUMN_C_COLUMNA = "cColumna";



    public static final String URL_NACION_RECIBIR_TRANSMISION ="acta/recibir-transmision/";




    public static final String MENSAJE_ACTA_PENDIENTE_DIGITALIZACION = "ACTA PENDIENTE DE DIGITALIZACIÓN";

    public static final String MENSAJE_ACTA_SE_ENCUENTRA_ESTADO_PENDIENTE = "El acta se encuentra en un estado pendiente.";


    public static final String REFLECT_NAME_METHOD_GET_VOTOS = "getVotos";
    
    //Oficio
    public static final String OFICIO_FECHA = "fechaOficio";
    public static final String OFICIO_NUMERO = "numeroOficio";
    public static final String OFICIO_DESTINATARIO = "destinatario";
    public static final String OFICIO_CARGO = "cargo";
    public static final String OFICIO_DIRECCION = "direccion";
    public static final String OFICIO_ASUNTO = "asunto";
    public static final String OFICIO_NOMBRE_PROCESO = "nombreProceso";
    public static final String OFICIO_CANTIDAD_ACTAS = "cantidadActas";
    public static final String OFICIO_CANTIDAD_TEXTO = "cantidadTexto";
    public static final String OFICIO_CODIGO_VERIFICACION = "codigoVerificacion";

    //Reportes
    public static final String CC_NACION_DESCRIPCION = "NACION";
    public static final String ORGANIZACIONES_POLITICAS = "OrganizacionesPoliticas.jrxml";
    public static final String CANDIDATOS_ORG_POL = "CandidatosOrgPol.jrxml";
    public static final String AUTORIDADES_EN_CONSULTA = "AutoridadesConsulta.jrxml";
    public static final String LISTA_MESAS_UBIGEO_REPORT_JRXML =  "ListaMesasUbigeo.jrxml";
    public static final String REPORTE_AVANCE_DIGITALIZACION_ACTAS = "AvanceDigitalizacion.jrxml";
    public static final String REPORTE_ESTADO_ACTAS_ODPE = "EstadoActasOdpe.jrxml";
    public static final String REPORTE_ACTAS_DIGITALIZADAS = "ActasDigitalizadas.jrxml";
    public static final String REPORTE_ACTAS_DIGITALIZADAS_EXCEL = "ActasDigitalizadasExcel.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML =  "ResultadoActasContabilizadas.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_9 =  "ResultadoActasContabilizadas_9.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_18 =  "ResultadoActasContabilizadas_18.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_27 =  "ResultadoActasContabilizadas_27.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_REPORT_JRXML_36 =  "ResultadoActasContabilizadas_36.jrxml";
    public static final String RESULTADO_ACTAS_CONTABILIZADAS_CPR_REPORT_JRXML =  "ResultadoActasContabilizadas_CPR.jrxml";
    public static final String RESUMEN_TOTAL_PORCENTAJES_REPORT_JRXML =  "ResumenTotalPorcentajes.jrxml";
    public static final String RESUMEN_TOTAL_CIFRAS_REPORT_JRXML =  "ResumenTotalCifras.jrxml";
    public static final String REPORTE_MESAS_ESTADO_MESA = "ReporteMesasEstadoMesa.jrxml";
    public static final String REPORTE_MESAS_ESTADO_MESA_X_ESTADO = "ReporteMesasEstadoMesaxEstado.jrxml";
    public static final String REPORTE_MESAS_ESTADO_ACTA = "ReporteMesasEstadoActa.jrxml";
    public static final String REPORTE_MESAS_ESTADO_ACTA_X_ESTADO = "ReporteMesasEstadoActaxEstado.jrxml";
    public static final String REPORTE_MESAS_ESTADO_DIGITACION = "ReporteMesasEstadoDigitacion.jrxml";
    public static final String REPORTE_MESAS_ESTADO_DIGITACION_X_ESTADO = "ReporteMesasEstadoDigitacionxEstado.jrxml";
    public static final String REPORTE_DETALLE_AVANCE_REGISTRO_UBIGEO = "DetalleAvanceRegistroUbigeo.jrxml";
    public static final String REPORTE_TOTAL_ACTAS_ENVIADAS_JEE_CENTROCOMPUTO = "TotalActasEnviadasJEECentroComputo.jrxml";

    public static final String REPORTE_AUDITORIA_DIGITACION = "ReporteAuditoriaDigitacion.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL = "ReporteAuditoriaDigitacionPreferencial_34.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_9 = "ReporteAuditoriaDigitacionPreferencial_9.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_16 = "ReporteAuditoriaDigitacionPreferencial_16.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_27 = "ReporteAuditoriaDigitacionPreferencial_27.jrxml";
    public static final String REPORTE_AUDITORIA_DIGITACION_REVOCATORIA = "ReporteAuditoriaDigitacion-CPR.jrxml";

    public static final String REPORTE_TRANSACCIONES_REALIZADAS = "TransaccionesRealizadas.jrxml";
    public static final String REPORTE_AVANCE_DIGITALIZACION_RESOLUCION = "AvanceDigitalizacionResolucion.jrxml";
    public static final String AVANCE_MESA_CPR_REPORT_JRXML =  "AvanceMesaCPR.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML =  "AvanceMesaPreferencial.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_9 =  "AvanceMesaPreferencial_9.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_18 =  "AvanceMesaPreferencial_18.jrxml";
    public static final String AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_27 =  "AvanceMesaPreferencial_27.jrxml";
    public static final String REPORTE_RELACION_PUESTA_CERO = "RelacionPuestaCero.jrxml";
    public static final String REPORTE_HISTORICO_CIERRE_REAPERTURA = "HistoricoCierreReapertura.jrxml";
    public static final String REPORTE_CIERRE_ACTIVIDADES = "CierreActividades.jrxml";
    public static final String REPORTE_REAPERTURA_ACTIVIDADES = "ReaperturaActividades.jrxml";
    public static final String REPORTE_AUTORIDADES_REVOCADAS = "AutoridadesRevocadas.jrxml";
    public static final String REPORTE_ASISTENCIA_MIEMBROS_MESA = "AsistenciaMiembroMesa.jrxml";
    public static final String REPORTE_ASISTENCIA_PERSONEROS = "AsistenciaPersoneros.jrxml";
    public static final String REPORTE_ASISTENCIA_MM_ESCRUTINIO = "AsistenciaMMEscrutinio.jrxml";
    public static final String REPORTE_MESAS_OBSERVACIONES = "RPT060310-41-ReporteMesasObservaciones.jrxml";
    public static final String REPORTE_AVANCE_DIGITALIZACION_LE_HA = "RPT06030103_avance_digitalizacion.jrxml";
  public static final String REPORTE_AVANCE_DIGITALIZACION_LE_DEN = "RPT06030103_avance_digitalizacion_den.jrxml";
    public static final String PRODUCTIVIDAD_DIGITADOR_JRXML = "ProductividadDigitador.jrxml";
    public static final String REPORTE_LISTA_USUARIOS = "ReporteListaUsuarios.jrxml";
  public static final String REPORTE_DETALLE_AVANCE_MIEMBROS_MESA_ESCRUTINIO = "DetalleAvanceMiembrosMesaEscrutinio.jrxml";
  public static final String REPORTE_DETALLE_AVANCE_PERSONEROS = "DetalleAvancePersoneros.jrxml";


    public static final String TITULO_PRINCIPAL = "ELECCIONES GENERALES 2026";
    public static final String TITULO_ELECCION_VERIFICACION_DIGITACION_ACTA = "VERIFICACIÓN DE DIGITACIÓN DEL ACTA";
    public static final String TITULO_REPORTE_MESAS_ESTADO_MESA = "LISTADO DE MESAS POR ESTADO DE MESA";
    public static final String TITULO_REPORTE_MESAS_ESTADO_ACTA = "LISTADO DE MESAS POR ESTADO DE ACTA";
    public static final String TITULO_REPORTE_MESAS_ESTADO_DIGITACION = "LISTADO DE MESAS POR ESTADO DE DIGITACIÓN";
    public static final String TITULO_REPORTE_INFORMACION_OFICIAL = "MONITOREO DE INFORMACIÓN OFICIAL";
    public static final String TITULO_REPORTE_COMPARACION_OMISOS_AUSENTISMO = "COMPARACIÓN OMISOS VS AUSENTISMO";




    public static final Integer CANTIDAD_VOTOS_PREFERENCIALES_PARLAMENTO = 16;

    public static final String REPORTE_INFORMACION_OFICIAL = "InformacionOficial.jrxml";
    public static final String REPORTE_OMISOS_DETALLE = "RPT06030101_ListaOmisosDetalle.jrxml";
    public static final String REPORTE_OMISOS_RESUMEN = "RPT06030102_ListaOmisosResumen.jrxml";
    public static final String REPORTE_COMPARACION_OMISOS_AUSENTISMO = "RPT06030603-TipoComparacionTodos.jrxml";
    public static final String REPORTE_MESAS_SIN_OMISOS = "MesasSinOmisos.jrxml";


    public static final String REPORTE_ACTAS_NO_DEVUELTAS_ACTAS = "ActasNoDevueltas-acta.jrxml";
    public static final String REPORTE_ACTAS_NO_DEVUELTAS_TODAS = "ActasNoDevueltas-todas.jrxml";
    public static final String REPORTE_LISTA_PARTICIPANTES = "ListaParticipantes.jrxml";
    public static final String REPORTE_TRANSMISION = "Transmision.jrxml";
    
    public static final String SISTEMAS_AUTOMATIZADOS_JRXML = "SistemasAutomatizados.jrxml";
    public static final String LISTADO_PCS_JRXML = "ListadoPcs.jrxml";

  public static final String REPORTE_RESUMEN_ESTADO_ACTAS = "ResumenEstadoActas.jrxml";
    
    public static final Integer ELECCION_PRESIDENCIAL = 10;
    public static final Integer ELECCION_PARLAMENTO_ANDINO = 12;
    public static final Integer ELECCION_CPR = 7;
    public static final Integer ELECCION_ECM = 4;

    public static final String NOMBRE_LOGO_ONPE = "onpe.jpg";
    public static final Integer SASA_RESPUESTA_ERROR = -1;
    public static final String SASA_TOKEN_NO_EXISTENTE = "SASA: Token No existente, vuelva a iniciar sesión";
    public static final String SASA_TOKEN_EXPIRADO_MENSAJE = "SASA: Token inválido, vuelva a iniciar sesión";
    public static final String SASA_SERVICIO_NO_DISPONIBLE = "SASA no disponible";
    public static final String SASA_ERROR_CAMBIO_CONSTRASENIA = "No se puede realizar el cambio de contraseña.";
    public static final String SASA_ERROR_ACTUALIZAR_DATOS = "Error al actualizar datos SASA";
    public static final Set<HttpStatusCode> SASA_TOKEN_INVALIDO_O_EXPIRADO_HTTP_STATUS = Set.of(HttpStatus.FORBIDDEN, HttpStatus.METHOD_NOT_ALLOWED);

    public static final Integer LOG_TRANSACCIONES_AUTORIZACION_SI = 1;
    public static final Integer LOG_TRANSACCIONES_AUTORIZACION_NO = 0;
    public static final Integer LOG_TRANSACCIONES_ACCION = 1;
    public static final String LOG_TRANSACCIONES_TIPO_REPORTE = "reporte";

    public static final Integer METODO_REQUIERE_AUTORIAZION = 1;
    public static final Integer METODO_NO_REQUIERE_AUTORIAZION = 0;

    public static final Integer SESION_ACTIVO = 1;
    public static final String SESION_CERRADA_POR_USUARIO = "Tu sesión fue cerrada desde otra pestaña.";
    public static final String SESION_CERRADA_POR_ADMINISTRADOR = "Tu sesión fue cerrada por el administrador %s.";
    public static final String SESION_CERRADA_POR_OTRO_DISPOSITIVO = "Tu sesión fue cerrada porque se inició sesión desde otro dispositivo.";
    
    public static final Integer ESTADO_TRANSMISION_OK = 1;
    public static final Integer ESTADO_TRANSMISION_ERROR = 0;
    public static final Integer ESTADO_TRANSMISION_EJECUTANDOSE = 2;
    public static final Integer CERO_INTENTO_TRANSMISION = 0;



    //LONGITUDES CARACTERES
  public static final Integer LONGITUD_RANGOS_COPIAS_AE = 11;
  public static final Integer LONGITUD_MESA = 6;
  public static final Integer CANTIDAD_MIEMBROS_SORTEADOS_CPR = 6;
  public static final Integer CANTIDAD_MIEMBROS_SORTEADOS_NO_CPR = 9;
  public static final Integer LONGITUD_COPIA = 2;
  public static final Integer LONGITUD_MESA_MAS_COPIA = 8;

  //Util
  public static final Integer DIAS_PREVIOS_MARCA_DE_AGUA = -2;

  public static final String TIPO_PASAR_NULOS_ELECTORES_HABILES = "1";
  public static final String TIPO_PASAR_NULOS_CVAS = "2";
  public static final String TIPO_PASAR_NULOS_SUMA_VOTOS = "3";


  public static final String ESTADO_CAMBIO_RESOL_ANTES = "A";
  public static final String ESTADO_CAMBIO_RESOL_DESPUES = "D";
  
  public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA = "AC";
  public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_RESOLUCION = "RE";
  public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_LISTA_ELECTORES = "LE";
  public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_HOJA_ASISTENCIA = "HA";
  public static final String CONTROL_CALIDAD_TIPO_DOCUMENTO_DENUNCIAS = "DE";

  public static final String ACCESO_PC_TIPO_DOCUMENTO_ID_PC = "IDPC";
  
  public static final Integer CANTIDAD_ACTAS_ASIGNAR_CONTROL_CALIDAD = 5;

  public static final Integer ID_DOCUMENT_LISTA_ELECTORES = 7;
  public static final Integer ID_DOCUMENT_MIEMBRO_MESA = 8;
  public static final Integer CANTIDAD_BORRAR_BACKUP = 20;
  public static final String CARPETA_RESPALDO= "/respaldo";
  public static final String CARPETA_RESPALDO_SIN= "respaldo/";

  public static final Integer ACTA_INSTALACION_SUFRAGIO_FIRMA = 2;
  public static final Integer ACTA_ESCRUTINIO_FIRMA = 1;
  
  public static final Integer ACTAS_TRANSMISION_EJECUCION = 2;
  
  public static final Integer ACTAS_TRANSMISION_HABILITADA = 1;
  
  
}
