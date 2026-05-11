package pe.gob.onpe.sceorcbackend.utils;

import java.util.HashMap;
import java.util.Map;

public class ConstantesEstadoMesa {

    private ConstantesEstadoMesa() {
    }

    public static final String POR_INFORMAR = "A";
    public static final String INSTALADA = "B";
    public static final String NO_INSTALADA = "C";
    public static final String REPROCESAR = "R";
    public static final String IS_EDIT = "E";
    public static final String PROCESADA = "B";


    public static final String C_ESTADO_DIGTAL_PENDIENTE = "P";
    public static final String C_ESTADO_DIGTAL_DIGITALIZADA = "D";
    public static final String C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE = "Q";
    public static final String C_ESTADO_DIGTAL_APROBADA_COMPLETA = "C";
    public static final String C_ESTADO_DIGTAL_DIGITALIZADA_CON_PERDIDA_PARCIAL = "V";
    public static final String C_ESTADO_DIGTAL_APROBADA_CON_PERDIDA_PARCIAL = "S";
    public static final String C_ESTADO_DIGTAL_PERDIDA_TOTAL = "L";
    public static final String C_ESTADO_DIGTAL_RECHAZADA = "O";
    public static final String C_ESTADO_DIGTAL_PROCESADO = "B";

    private static final Map<String, String> MAP_ESTADO_MESA = new HashMap<>();

    static {
        MAP_ESTADO_MESA.put(POR_INFORMAR, "por informar");
        MAP_ESTADO_MESA.put(INSTALADA, "instalada");
        MAP_ESTADO_MESA.put(NO_INSTALADA, "no instalada");
    }


    public static Map<String, String> getMapEstadoMesa() {
        return MAP_ESTADO_MESA;
    }


}
