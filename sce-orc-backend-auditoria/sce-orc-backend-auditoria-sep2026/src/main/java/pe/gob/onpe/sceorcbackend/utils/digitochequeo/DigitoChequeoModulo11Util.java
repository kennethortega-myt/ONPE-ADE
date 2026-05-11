package pe.gob.onpe.sceorcbackend.utils.digitochequeo;

import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

public class DigitoChequeoModulo11Util {

    private DigitoChequeoModulo11Util() {
    }

    private static final char DIG_ERROR = 'W';
    private static final int[] valores = {89, 3, 11, 17, 2, 65, 13, 23, 77};

    public static char obtenerDigitoChequeoAE(String nroMesa,String copia, ConfigRango config) {

        if (nroMesa == null || copia == null || nroMesa.length() != ConstantesComunes.LONGITUD_MESA  || copia.length() != ConstantesComunes.LONGITUD_COPIA|| config == null) {
            return DIG_ERROR;
        }

        final int nCopia;

        try {
            nCopia = Integer.parseInt(copia);
        } catch (NumberFormatException e) {
            return DIG_ERROR;
        }

        if (nCopia < config.getRangoInicial() || nCopia > config.getRangoFinal()) {
            return DIG_ERROR;
        }

        return calcularDigitoChequeo(nroMesa.concat(copia), config);
    }

    // Método auxiliar común
    private static char calcularDigitoChequeo(String pNumActaCopia, ConfigRango config) {
        if (config.getRangoCopias() == null || config.getRangoCopias().length() != ConstantesComunes.LONGITUD_RANGOS_COPIAS_AE) {
            return DIG_ERROR;
        }

        for (char c : pNumActaCopia.toCharArray()) {
            if (!Character.isDigit(c)) return DIG_ERROR;
        }

        int nAcumulad = 0;
        for (int i = 0; i < ConstantesComunes.LONGITUD_MESA_MAS_COPIA; ++i) {
            nAcumulad += (pNumActaCopia.charAt(i) - '0') * valores[i];
        }

        return config.getRangoCopias().charAt(nAcumulad % ConstantesComunes.LONGITUD_RANGOS_COPIAS_AE);
    }
}
