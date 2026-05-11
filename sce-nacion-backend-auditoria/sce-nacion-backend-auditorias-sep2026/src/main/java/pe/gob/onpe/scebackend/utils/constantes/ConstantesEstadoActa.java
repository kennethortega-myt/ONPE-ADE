package pe.gob.onpe.scebackend.utils.constantes;

public class ConstantesEstadoActa {

	private ConstantesEstadoActa() {
			throw new UnsupportedOperationException("ConstantesEstadoActa es una clase utilitaria y no puede ser instanciada");
		}


	public static final String ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION = "P";
	public static final String ESTADO_DIGTAL_DIGITALIZADA = "D";
	public static final String ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO = "K";
	public static final String ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA = "C";
	public static final String ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA = "O";
	public static final String ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA = "X";
	public static final String ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA = "B";


	//C_ESTADO_ACTA
	public static final String ESTADO_ACTA_PENDIENTE = "A";
	public static final String ESTADO_ACTA_PRIMER_CONTROL = "B";
	public static final String ESTADO_ACTA_DIGITALIZADA = "T";//
	public static final String ESTADO_ACTA_DIGITADA = "C";//
	public static final String ESTADO_ACTA_PROCESADA = "D";
	public static final String ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR = "E";//DIGITACIONES EN GENERAL CON ERROR
	public static final String ESTADO_ACTA_SEGUNDA_VERIFICACION = "W";
	public static final String ESTADO_ACTA_PARA_ENVIO_AL_JURADO = "H";//ACTA OBSERVADA - SON LAS QUE SE ENVIAN AL JURADO
	public static final String ESTADO_ACTA_ENVIADA_A_JEE = "I";
	public static final String ESTADO_ACTA_ACTA_DEVUELTA = "J";
	public static final String ESTADO_ACTA_ASOCIADA_A_RESOLUCION = "K";

	public static final String ESTADO_ACTA_PROCESADA_POR_RESOLUCION = "L";
	public static final String ESTADO_ACTA_ANULADA = "M";
	public static final String ESTADO_ACTA_MESA_NO_INSTALADA = "N";
	public static final String ESTADO_ACTA_EXTRAVIADA = "O";
	public static final String ESTADO_ACTA_SINIESTRADA = "S";
	public static final String ESTADO_ACTA_REPROCESADA_NORMAL = "Q";
	public static final String ESTADO_ACTA_REPROCESADA_ANULADA = "R";


	public static final String ESTADO_COMPUTO_ACTA_CONTABILIZADA = "S";
	public static final String ESTADO_COMPUTO_ACTA_PENDIENTE = "N";
	public static final String ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA = "O";
	public static final String ESTADO_COMPUTO_ACTA_EN_PROCESO = "P";
	public static final String ESTADO_COMPUTO_ACTA_OBSERVADA_IMPUGNADA = "E";



	public static final String ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL = "G";
	public static final String ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL = "P";
	public static final String ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS = "L";

	public static final String ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL = "M";
	public static final String ESTADO_ACTA_RESOLUCION_ERROR_MAT_PREFERENCIAL = "V";
	public static final String ESTADO_ACTA_RESOLUCION_ERROR_MAT = "E";

	public static final String ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA = "I";
	public static final String ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD = "N";
	public static final String ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS = "S";
	public static final String ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA = "C";
	public static final String ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA = "X";
	public static final String ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA = "Y";
	public static final String ESTADO_ACTA_RESOLUCION_ACTA_OBS_MESA = "H";
	public static final String ESTADO_ACTA_RESOLUCION_ACTA_REPROCESO = "R";
	public static final String ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA = "F";




	//ERRORES MATERIALES A NIVEL DE ACTA
	/**
	 * Total de Ciudadanos que Votaron es mayor que Total de Electores Hábiles
	 */
	public static final String ESTADO_ERROR_MATERIAL_A = "A";

	/**
	 * Total de votos mayor que Total de Electores Hábiles
	 */
	public static final String ESTADO_ERROR_MATERIAL_B = "B";

	/**
	 * Total de votos es menor que el Total de Ciudadanos que Votaron y Total de Ciudadanos que Votaron menor o igual al Total de Electores Hábiles
	 */
	public static final String ESTADO_ERROR_MATERIAL_D = "D";

	/**
	 * Total de votos es mayor que el Total de Ciudadanos que Votaron y Total de Ciudadanos que Votaron menor o igual al Total de Electores Hábiles
	 */
	public static final String ESTADO_ERROR_MATERIAL_E = "E";


	//ERRORES MATERIALES A NIVEL DE ORGANIZACION POLITICA
	/**
	 * Votación consignada a favor de una determinada organización política es mayor que total de Electores Hábiles
	 */
	public static final String ESTADO_ERROR_MATERIAL_C = "C";

	/**
	 * Votación consignada a favor de una determinada organización política es mayor que el total de Ciudadanos que Votaron
	 */
	public static final String ESTADO_ERROR_MATERIAL_F = "F";



	//ERRORES MATERIALES A NIVEL DE VOTOS PREFERENCIALES
	/**
	 * Votación Preferencial de un candidato es mayor que la cantidad de votos de su organización política
	 */
	public static final String ESTADO_ERROR_MATERIAL_G = "G";

	/**
	 *Votación Preferencial de un candidato es mayor al Total de Ciudadanos que Votaron
	 */
	public static final String ESTADO_ERROR_MATERIAL_H = "H";

	/**
	 *Votación Preferencial de un candidato es mayor al Total de Electores Hábiles
	 */
	public static final String ESTADO_ERROR_MATERIAL_I = "I";

	/**
	 *Votación Preferencial de un candidato es mayor al Total de votos de las organizaciones políticas
	 */
	public static final String ESTADO_ERROR_MATERIAL_J = "J";

	/**
	 *Suma Total de Votos Preferenciales de los candidatos de una organización política es mayor al doble de la votación de la misma organización política
	 */
	public static final String ESTADO_ERROR_MATERIAL_K = "K";

	/**
	 * *Suma Total de Votos Preferenciales de los candidatos de una organización política es mayor a la votación de la misma organización política
	 */
	public static final String ESTADO_ERROR_MATERIAL_M = "M";

	public static final String ESTADO_ERROR_MATERIAL_N_ILEGIBLE_PREFERENCIAL = "N";
	public static final String ESTADO_ERROR_MATERIAL_O_ILEGIBLE_CVAS = "O";
	public static final String ESTADO_ERROR_MATERIAL_P_ILEGIBLE_AGRUPOL = "P";



	public static final String ESTADO_ERROR_MATERIAL_V = "V";
	public static final String ESTADO_ERROR_MATERIAL_W = "W";
	public static final String ESTADO_ERROR_MATERIAL_I_REV = "I";
	public static final String ESTADO_ERROR_MATERIAL_R = "R";
	public static final String ESTADO_ERROR_MATERIAL_S = "S";
	public static final String ESTADO_ERROR_MATERIAL_Y = "Y";





}