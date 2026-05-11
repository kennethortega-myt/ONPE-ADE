package pe.gob.onpe.scebackend.utils;


public class PathUtils {
    private PathUtils() {
        throw new UnsupportedOperationException("PathUtils es una clase utilitaria y no debe ser instanciada");
    }
	public static String normalizePath(String path, String fileName) {
        if (path.endsWith("/") || path.endsWith("\\")) {
            return path + fileName;
        } else {
            return path + "/" + fileName;
        }
    }
	
	
}
