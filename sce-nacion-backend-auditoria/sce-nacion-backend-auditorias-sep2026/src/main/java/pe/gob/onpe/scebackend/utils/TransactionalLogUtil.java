package pe.gob.onpe.scebackend.utils;

public class TransactionalLogUtil {

    private TransactionalLogUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.toLowerCase().split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
                result.append(" ");
            }
        }

        // Eliminar el espacio extra al final
        return result.toString().trim();
    }

    public static String crearMensajeLog(String input) {
        String pascalCaseString = toPascalCase(input);
        return "Se consultó el reporte " + pascalCaseString;
    }

}
