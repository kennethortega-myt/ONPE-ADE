package pe.gob.onpe.scebatchpr.utils;

import java.util.UUID;

public class Utils {
	
	private Utils() {}

	public static String truncate(String input, int numero) {
        if (input == null) {
            return null;
        }
        if (input.length() <= numero) {
            return input;
        }
        return input.substring(0, numero);
    }
	
	public static String uuidTime() {
		long currentTimeMillis = System.currentTimeMillis();
        long randomValue = UUID.randomUUID().getLeastSignificantBits();

        long mostSigBits = currentTimeMillis << 32;
        long leastSigBits = randomValue & 0xFFFFFFFF;

        UUID uuid = new UUID(mostSigBits, leastSigBits);
		return uuid.toString();
	}
}
