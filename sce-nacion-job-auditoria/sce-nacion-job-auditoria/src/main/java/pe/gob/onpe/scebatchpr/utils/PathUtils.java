package pe.gob.onpe.scebatchpr.utils;


public class PathUtils {
	
	private PathUtils() {}

	public static String normalizePath(String path, String fileName) {
        if (path.endsWith("/") || path.endsWith("\\")) {
            return path + fileName;
        } else {
            return path + "/" + fileName;
        }
    }
	
	
}