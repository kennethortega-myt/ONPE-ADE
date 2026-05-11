package pe.gob.onpe.sceorcbackend.utils;

public class ConstantesReportes {

	private ConstantesReportes(){}
	
	public static final String MESAS_UBIGEO_REPORT_JRXML = "MesasPorUbigeo.jrxml";
	public static final String ORGANIZACIONES_POLITICAS_REPORT_JRXML = "OrganizacionesPoliticas.jrxml";
	public static final String CANDIDATOS_ORG_POLITICAS_REPORT_JRXML = "CandidatosOrgPol.jrxml";

	public static final String NOMBRE_SERVIDOR_BD = "";
	public static final String nameReporteMesaEstadoActa = "MesaEstadoActa";
	public static final String nameReporteMesaEstadoDigitacion = "MesaEstadoDigitacion";
	public static final String nameReporteMesaEstadoMesa = "MesaEstadoMesa";
	public static final String nameReporteAuditoriaDigitacion = "AuditoriaDigitacion";
	public static final String nameReporteAuditoriaDigitacionPreferencial = "AuditoriaDigitacionPreferencial";
	public static final String CODIGO_UBIGEO_NACION = "000000";
	public static final String nameReporteAvanceMesaPorMesa = "AvanceMesaPorMesa";
    public static final String nameReporteAvanceMesaPorMesaPreferencial = "AvanceMesaPorMesaPreferencial";
	public static final String nameReporteInformacionOficial = "InformacionOficial";
	public static final String nameReporteOmisosElectoresResumen = "OmisosElectoresResumen";
	public static final String nameReporteOmisosMMResumen = "OmisosMMResumen";
	public static final String nameReporteOmisosPersonerosResumen = "OmisosPersonerosResumen";
	public static final String NAME_REPORTE_OMISOS_MM_ACTA_ESC_RESUMEN = "OmisosMMActaEscResumen";
	public static final String nameReporteOmisosElectoresDetalle = "OmisosElectoresDetalle";
	public static final String nameReporteOmisosMMDetalle = "OmisosMMDetalle";
	public static final String nameReporteOmisosPersonerosDetalle = "OmisosPersonerosDetalle";
	public static final String NAME_REPORTE_OMISOS_MM_ACTA_ESC_DETALLE = "OmisosMMActaEscDetalle";
	public static final String nameReporteMesasObservaciones = "MesasObservaciones";
	public static final String nameReporteComparacionOmisosAusentismo = "OmisosAusentismo";
    public static final String nameReporteAvanceDigitalizacionLe = "AvanceDigitalizacionLe";
    public static final String nameReporteAvanceDigitalizacionHa = "AvanceDigitalizacionHa";
    public static final String nameReporteAvanceDigitalizacionDenuncias = "AvanceDigitalizacionDenuncias";


	public static final String nombreColumnaOmisosElectores = "Total Electores";
	public static final String nombreColumnaOmisosMiembrosMesa = "Total Miembros Mesa";
	public static final String NOMBRE_COLUMNA_OMISOS_MIEMBROS_MESA_ACTA_ESCRUTINIO = "Total Miembros Mesa";
	public static final String nombreColumnaOmisosPersoneros = "Total Personeros";

	public static String TITULO_REPORTE_OMISOS_ELECTORES = "AVANCE DE REGISTRO DE OMISOS POR CENTRO CÓMPUTO (SÓLO VOTANTES)";
	public static String TITULO_REPORTE_OMISOS_MIEMBROS_MESA = "AVANCE DE REGISTRO DE OMISOS POR CENTRO CÓMPUTO (SÓLO MIEMBROS)";
	public static String TITULO_REPORTE_OMISOS_PERSONEROS = "AVANCE DE REGISTRO DE PERSONEROS POR CENTRO CÓMPUTO";
	public static String TITULO_REPORTE_OMISOS_MIEMBROS_MESA_ACTA_ESCRUTINIO = "AVANCE DE REGISTRO DE MIEMBROS DE MESA SEGÚN ACTA DE ESCRUTINIO POR CENTRO DE CÓMPUTO";
	public static String TITULO_REPORTE_MESAS_OBSERVACIONES = "MESAS CON OBSERVACIONES";
	public static String TITULO_REPORTE_MESAS_SIN_OMISOS_ELECTORES = "RESUMEN LISTADO DE MESAS SIN OMISOS (SÓLO VOTANTES)";
	public static String TITULO_REPORTE_MESAS_SIN_OMISOS_MIEMBROS_MESA = "RESUMEN LISTADO DE MESAS SIN OMISOS (SÓLO MIEMBROS)";

	public static final String TITULO_REPORTE_RESULTADO_ACTAS_CONTABILIZADAS_AVANCE = "AVANCE DE RESULTADO DE ACTAS CONTABILIZADAS";
	public static final String TITULO_REPORTE_RESULTADO_ACTAS_CONTABILIZADAS_100 = "RESULTADO DE ACTAS CONTABILIZADAS";
	
    public static final String TITULO_REPORTE_LISTADO_MESAS_UBIGEO = "LISTADO DE MESAS POR UBIGEO";

	public static final String TODOS = "TODOS";
    public static final Integer ID_AMBITO_ELECTORAL_NACION = 0;
    public static final Integer ID_CENTRO_COMPUTO_NACION = 0;

	public static final String CC_NACION_DESCRIPCION = "NACIÓN";

	public static final String DATA_NO_ENCONTRADA = "No existen coincidencias para el filtro seleccionado";

    public static String TITULO_REPORTE_AVANCE_DIGITALIZACION_LE = "AVANCE DE DIGITALIZACIÓN DE LISTA DE ELECTORES";
    public static String TITULO_REPORTE_AVANCE_DIGITALIZACION_HA = "AVANCE DE DIGITALIZACIÓN DE LISTA DE HOJAS DE ASISTENCIA";
    public static String TITULO_REPORTE_AVANCE_DIGITALIZACION_DENUNCIAS = "AVANCE DE DIGITALIZACIÓN DE DENUNCIAS";
    public static String TITULO_REPORTE_LISTA_USUARIOS = "LISTA DE USUARIOS";

	public static final String REPORT_PARAM_SIN_VALOR_OFICIAL = "sinvaloroficial";
	public static final String REPORT_PARAM_URL_IMAGE = "url_imagen";
	public static final String REPORT_PARAM_PIXEL_TRANSPARENTE = "pixeltransparente";
	public static final String REPORT_PARAM_VERSION = "version";
	public static final String REPORT_PARAM_USUARIO = "usuario";
	public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_CONTROL_DIGITALIZACION = "PrecisionAsisteAutomaControlDigitalizacion.jrxml";
	public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_RESUMEN = "PrecisionAsisteAutomaDigitacionResumen.jrxml";
	public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_DETALLE_PRESIDENCIAL = "PrecisionAsisteAutomaDigitacionDetallePresidencial.jrxml";
	public static final String REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_DETALLE_NO_PRESIDENCIAL = "PrecisionAsisteAutomaDigitacionDetalleNoPresidencial.jrxml";
	public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA = "ComparacionDigitacionAutomatica.jrxml";
    public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_9 = "ComparacionDigitacionAsistenteAutomatizadoPref_9.jrxml";
    public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_16 = "ComparacionDigitacionAsistenteAutomatizadoPref_16.jrxml";
    public static final String REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_32 = "ComparacionDigitacionAsistenteAutomatizadoPref_32.jrxml";

	public static final String PATH_IMAGE_COMMON_NAC = "pe/gob/onpe/sceorcbackend/common/image/";
	public static final Integer LOG_TRANSACCIONES_AUTORIZACION_SI = 1;
	public static final Integer LOG_TRANSACCIONES_AUTORIZACION_NO = 0;
	public static final Integer LOG_TRANSACCIONES_ACCION = 1;
	public static final String LOG_TRANSACCIONES_TIPO_REPORTE = "reporte";

	public static final String NOMBRE_PARAMETRO_DET_RESOLUCION_JNE = "p_numero_resolucion_jne";
}
