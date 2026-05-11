package pe.gob.onpe.sceorcbackend.utils;

public class ConstantesTipoDocumentoElectoral {

	private ConstantesTipoDocumentoElectoral() {
		throw new UnsupportedOperationException("ConstantesTipoDocumentoElectoral es una clase utilitaria y no puede ser instanciada");
	}
	
	public static final Integer ACTA_DE_ESCRUTINIO = 1;
	public static final Integer ACTA_INSTALACION_Y_SUFRAGIO = 2;
	public static final Integer ACTA_INSTALACION = 3;
	public static final Integer ACTA_SUFRAGIO = 4;
	public static final Integer RESOLUCION = 5;
	
}
