package pe.gob.onpe.scebackend.utils.constantes;

public class ConstanteTransmision {
	private ConstanteTransmision() {
		throw new UnsupportedOperationException("ConstanteAccionTransmision es una clase utilitaria y no puede ser instanciada");
	}
	public static final String ACCION_PUESTA_CERO = "PUESTA_CERO";
	public static final String ACCION_ACTA = "ACTA";
	public static final String ACCION_MESA_DETALLE = "MESA_DETALLE";
	public static final String ACCION_SOBRE_CELESTE = "SOBRE_CELESTE";
	public static final String FORMATO_FECHA = "dd-MM-yyyy HH:mm:ss.SSS";
	public static final String FORMATO_FECHA_MONITEREO = "dd-MM-yyyy HH:mm:ss";
	
    
    public static final Integer ESTADO_TRANSMISION_OK = 1;
    public static final Integer ESTADO_TRANSMISION_ERROR = 0;
    public static final Integer ESTADO_TRANSMISION_EJECUTANDOSE = 2;
    public static final Integer CERO_INTENTO_TRANSMISION = 0;
    
    public static final String SUBACCION_DENUNCIAS = "DENUNCIAS";
}
