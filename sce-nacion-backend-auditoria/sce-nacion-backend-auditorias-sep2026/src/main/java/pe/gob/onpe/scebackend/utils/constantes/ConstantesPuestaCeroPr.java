package pe.gob.onpe.scebackend.utils.constantes;

public class ConstantesPuestaCeroPr {

	private ConstantesPuestaCeroPr() {
		throw new UnsupportedOperationException("ConstantesPuestaCeroPr es una clase utilitaria y no puede ser instanciada");
		
	}
   public static final String EJECUTADO = "E";
   public static final String NO_EJECUTADO = "S";	
   
   public static final Integer INTENTO_INICIAL = 0;
   public static final Integer PRIMER_INTENTO = 1;
   public static final Integer ACTIVO = 1;
   public static final Integer NO_ACTIVO = 0;
	
}
