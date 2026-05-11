package pe.gob.onpe.scebackend.utils;



public class StringCustomUtils {

	private StringCustomUtils() {
        throw new UnsupportedOperationException("Esta clase no puede ser instanciada");
    }

    public static String emptyToNull(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return texto.trim();
    }
    
    public static String emptyToNull(String texto, String option) {
        if (texto == null || texto.trim().isEmpty() || texto.trim().equalsIgnoreCase(option)) {
            return null;
        }
        return texto.trim();
    }
    
}
