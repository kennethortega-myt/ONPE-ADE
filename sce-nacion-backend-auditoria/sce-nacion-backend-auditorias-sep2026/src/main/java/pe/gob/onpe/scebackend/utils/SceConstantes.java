package pe.gob.onpe.scebackend.utils;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.ColorDTO;

public class SceConstantes {

    private SceConstantes() {
        throw new UnsupportedOperationException("SceConstantes es una clase utilitaria y no debe ser instanciada");
    }

    public static final String UBIGEO_NACION = "000000";
    public static final Integer LENGTH_BEARER = 7;
    public static final String TIMEZONE = "America/Lima";

    public static final Integer VISIBLE = 1;

    public static final Integer NO_VISIBLE = 0;

    public static final Integer ACTIVO = 1;

    public static final Integer INACTIVO = 0;

    public static final Integer REQUERIDO = 1;

    public static final Integer NO_REQUERIDO = 0;

    public static final Integer HABILITADO = 1;

    public static final Integer NO_HABLITADO = 0;

    public static final Integer MAX_INTENTOS_JNE = 3;

    public static final List<ColorDTO> COLORES = List.of(new ColorDTO("#e92228", 1), new ColorDTO("#FDB81F", 2),
            new ColorDTO("#7D4B9E", 3), new ColorDTO("#76BD43", 4), new ColorDTO("#0DABE3", 5),
            new ColorDTO("#2971B9", 6), new ColorDTO("#F26B3E", 7), new ColorDTO("#7FFF00", 8),
            new ColorDTO("#ED80AD", 9), new ColorDTO("#00FFFF", 10), new ColorDTO("#FF8C00", 11),
            new ColorDTO("#9400D3", 12), new ColorDTO("#ADFF2F", 13), new ColorDTO("#008000", 14),
            new ColorDTO("#9900FF", 15), new ColorDTO("#FF6347", 16), new ColorDTO("#FFFF00", 17),
            new ColorDTO("#00CED1", 18), new ColorDTO("#8B008B", 19), new ColorDTO("#8B0000", 20),
            new ColorDTO("#00AAFF", 21), new ColorDTO("#FF1493", 22), new ColorDTO("#FF00FF", 23),
            new ColorDTO("#7B68EE", 24), new ColorDTO("#6B8E23", 25), new ColorDTO("#191970", 26),
            new ColorDTO("#00BFFF", 27), new ColorDTO("#FFD700", 28), new ColorDTO("#C71585", 29),
            new ColorDTO("#0400FF", 30));

    public static final String PERFIL_ADM_NAC = "ADM_NAC";
    public static final String PERFIL_REPO_NAC = "REPO_NAC";
    public static final String PERFIL_STAE = "STAE";
    public static final String PERFIL_EXTRANJERO = "EXTRANJERO";
    public static final String PERFIL_JNE = "JNE";

    public static final String PERFI_ADM_CC = "ADM_CC";

    public static final String FORMATO_FECHA = "dd-MM-yyyy HH:mm:ss";

    public static final String FORMATO_FECHA_REPORTE = "dd/MM/yyyy HH:mm:ss";

    public static final Integer IS_CONFIG_GENERAL = 1;
    public static final Integer NOT_CONFIG_GENERAL = 0;

    public static final String NAME_ZIP_ANEXO1 = "Anexo1.zip";
    public static final String NAME_TXT_VOTOS = "ONPE_TBL_VOTOS.txt";
    public static final String NAME_TXT_VOTOS_CIFRA = "ONPE_TBL_CIFRA.txt";
    public static final String NAME_TXT_VOTOS_CIFRA_INICIAL = "ONPE_TBL_CIFRA_INICIAL.txt";
    public static final String NAME_TXT_TABLA_ACTAS = "ONPE_TBL_COMPUACTAS.txt";
    public static final String NAME_TXT_MESAS_NO_INSTALADAS = "ONPE_TBL_MESANOINSTA.txt";
    public static final String NAME_ZIP_MAESTRAS_ORG = "Maestra.zip";

    public static final String NAME_TXT_UBIGEO = "ONPE_TBL_UBIGEO.txt";

    public static final String NAME_TXT_ODPE = "ONPE_TBL_ODPE.txt";
    public static final String NAME_ZIP_ANEXO2 = "Anexo2.zip";
    public static final String NAME_ZIP_ALL = "AnexosJne.zip";
    public static final String HORA_DEFAULT_DIA_D = "17:00:00";
    public static final String HORA_DEFAULT_FIN_DIA_D = "19:00:00";
    public static final String PARAMETRO_HORA_PROCESAMIENTO = "tp_hora_procesamiento";
    public static final String PARAMETRO_HORA_FIN_PROCESAMIENTO = "tp_hora_top_procesamiento";

    public static final String HEADER_IDSESSION = "IdSession";

}
