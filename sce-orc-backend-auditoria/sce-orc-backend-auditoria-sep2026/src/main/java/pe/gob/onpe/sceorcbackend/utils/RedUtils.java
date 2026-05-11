package pe.gob.onpe.sceorcbackend.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RedUtils {

	private RedUtils() {

    }
	
	public static String obtenerIpLocal() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return "IP desconocida";
        }
    }
	
	public static String obtenerNombreHost() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            return "Host desconocido";
        }
    }
	
}
