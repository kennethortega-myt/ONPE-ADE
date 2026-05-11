package pe.gob.onpe.sceorcbackend.utils;

import java.util.HashMap;
import java.util.Map;

public class ConstantesCatalogo {

    private ConstantesCatalogo() {

    }

    public static final String C_COLUMNA_ERROR_MATERIAL = "c_estado_error_material";
    public static final Integer DET_CAT_EST_ACTIVO = 1;

    /*Autorizacion inicio*/
    public static final String CAB_CAT_AUTH = "mae_autorizacion";
    public static final String DET_CAT_EST_COL_AUTH = "n_autorizacion";
    public static final Integer DET_CAT_EST_COD_AUTH_PCCC = 1;//Puesta a Cero de Centro de Cómputo
    public static final Integer DET_CAT_EST_COD_AUTH_PCAC = 2;//Puesta a Cero de Acta

    /*Autorizacion fin*/

    public static final String C_COLUMNA_TIPO_RESOLUCION = "n_tipo_resolucion";
    public static final String C_COLUMNA_TIPO_AMBITO_ELECTORAL = "n_tipo_ambito_electoral";
    public static final String C_COLUMNA_ESTADO_ACTA = "c_estado_acta";
    public static final String C_COLUMNA_ESTADO_MESA = "c_estado_mesa";
    public static final String C_COLUMNA_ESTADO_ACTA_RESOLUCION = "c_estado_acta_resolucion";


    public static final String C_COLUMNA_TIPO_FORMATO = "n_tipo_formato";



    public static final String CATALOGO_MAE_TIPO_DOCUMENTO_OTRO_DOC = "mae_tipo_documento_det_otro_documento";
    public static final String CATALOGO_DET_TIPO_DOCUMENTO_OTRO_DOC = "c_tipo_documento";

    public static final String CATALOGO_MAE_TIPO_PERDIDA_OTRO_DOC = "mae_tipo_perdida_det_otro_documento";
    public static final String CATALOGO_DET_TIPO_PERDIDA_OTRO_DOC = "c_tipo_perdida";

    public static final String MAE_ESTADOS_OTRO_DOCUMENTO = "mae_estado_otro_documento";
    public static final String DET_ESTADOS_OTRO_DOCUMENTO = "c_estado_documento";

    public static final String MAE_ESTADO_DIGITALIZACION_OTRO_DOCUMENTO = "mae_estado_otro_documento";
    public static final String DET_ESTADOS_DIGITALIZACION_OTRO_DOCUMENTO = "c_estado_digitalizacion";


    public static final String MAE_ESTADO_DIGITALIZACION_LE= "mae_estado_digitalizacion_le";
    public static final String DET_ESTADO_DIGITALIZACION_LE = "c_estado_digitalizacion_le";

    public static final String MAE_ESTADO_DIGITALIZACION_RESOLUCION = "mae_estado_digitalizacion_resolucion";
    public static final String DET_ESTADO_DIGITALIZACION_RESOLUCION = "c_estado_digitalizacion";

    public static final String MAE_ESTADO_RESOLUCION = "mae_estado_resolucion";
    public static final String DET_ESTADO_RESOLUCION = "c_estado_resolucion";


    public static final String MAE_SOLUCION_TECNOLOGICA = "mae_solucion_tecnologica";
    public static final String DET_SOLUCION_TECNOLOGICA = "n_solucion_tecnologica";


    public static final String MAE_TIPO_TRANSMISION = "mae_tipo_transmision";
    public static final String DET_TIPO_TRANSMISION = "n_tipo_transmision";

    public static final String MAE_PERFIL = "mae_perfil";
    public static final String DET_PERFIL = "c_perfil";
    public static final String PERFIL_NACION_ENDS_WITH = "_NAC";

    public static final String MAE_TIPO_DOCUMENTO_IDENTIDAD = "mae_tipo_documento_identidad_usuario";
    public static final String DET_TIPO_DOCUMENTO_IDENTIDAD = "n_tipo_documento_identidad";

    public static final String CATALOGO_MAE_TIPO_JEE = "mae_jurado_electoral_especial";
    public static final String CATALOGO_DET_TIPO_JEE = "c_id_jee";

    public static final Integer N_CODIGO_CARGO_ENTREGA_DEFAULT = -1;
    public static final Integer N_CODIGO_CARGO_ENTREGA_ENVIO_JEE = 1;
    public static final Integer N_CODIGO_CARGO_ENTREGA_ACTA_DEVUELTA = 2;
    public static final Integer N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA = 3;
    public static final Integer N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA = 4;
    public static final Integer N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA = 5;
    public static final Integer N_CODIGO_CARGO_ENTREGA_INFUNDADA = 6;
    public static final Integer N_CODIGO_CARGO_ENTREGA_INFUNDADA_XUBIGEO = 7;
    public static final Integer N_CODIGO_CARGO_ENTREGA_ANULADA_X_UBIGEO = 8;
    public static final Integer N_CODIGO_OFICIO_ACTA_OBSERVADA = 9;


    private static final Map<Integer, String> MAP_TIPOS_CARGOS = new HashMap<>();

    static {
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_ENVIO_JEE, "Actas enviadas al JEE");
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_ACTA_DEVUELTA, "Actas devueltas");
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA, "Mesa no instalada");
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA, "Actas extraviadas");
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA, "Acta siniestrada");
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_INFUNDADA, "Resolución Infundada");
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_INFUNDADA_XUBIGEO, "Resolución Infundada por Ubigeo");
        MAP_TIPOS_CARGOS.put(N_CODIGO_CARGO_ENTREGA_ANULADA_X_UBIGEO, "Resolución Anuladas por Ubigeo");
        MAP_TIPOS_CARGOS.put(N_CODIGO_OFICIO_ACTA_OBSERVADA, "Oficio Actas Observadas");
    }

    public static Map<Integer, String> getMapTiposCargos() {
        return MAP_TIPOS_CARGOS;
    }


    public static final Integer CATALOGO_PROCEDENCIA_JEE_COD = 1;
    public static final Integer CATALOGO_PROCEDENCIA_JNE_COD = 2;
    public static final Integer CATALOGO_PROCEDENCIA_ONPE_COD = 3;

    public static final Integer CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS = 1;
    public static final Integer CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE = 2;
    public static final Integer CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS = 3;

    public static final Integer CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO = 4;
    public static final Integer CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO = 5;
    public static final Integer CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS = 6;
    public static final Integer CATALOGO_TIPO_RESOL_INFUNDADAS = 7;
    public static final Integer CATALOGO_TIPO_RESOL_REPRO_JNE = 8;
    public static final Integer CATALOGO_TIPO_RESOL_REPRO_ONPE = 9;
    public static final Integer CATALOGO_TIPO_RESOL_OFICIO_ACTA_OBSERVADA = 10;
    public static final Integer CATALOGO_TIPO_RESOL_ACTA_DEVUELTA = 0;//para mantener logica se crea este tipo para actas devueltas

    private static final Map<Integer, String> MAP_TIPOS_RESOLUCIONES = new HashMap<>();

    static {
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS, "ACTAS EXTRAVIADAS");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE, "ACTAS ENVIADAS AL JEE");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS, "ACTAS SINIESTRADAS");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO, "ACTAS ANULADAS POR UBIGEO");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO, "INFUNDADAS POR UBIGEO");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS, "MESAS NO INSTALADAS");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_INFUNDADAS, "RESOLUCIONES INFUNDADAS");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_REPRO_JNE, "ACTAS REPROCESADAS POR EL JNE");
        MAP_TIPOS_RESOLUCIONES.put(CATALOGO_TIPO_RESOL_REPRO_ONPE, "ACTAS REPROCESADAS POR ONPE");
    }

    private static final Map<Integer, Integer> MAP_TIPO_RESOLUCION_CARGO_ENTREGA = new HashMap<>();

    static {
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS, N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE, N_CODIGO_CARGO_ENTREGA_ENVIO_JEE);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS, N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO, N_CODIGO_CARGO_ENTREGA_INFUNDADA_XUBIGEO);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS, N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_INFUNDADAS, N_CODIGO_CARGO_ENTREGA_INFUNDADA);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_ACTA_DEVUELTA, N_CODIGO_CARGO_ENTREGA_ACTA_DEVUELTA);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO, N_CODIGO_CARGO_ENTREGA_ANULADA_X_UBIGEO);
        MAP_TIPO_RESOLUCION_CARGO_ENTREGA.put(CATALOGO_TIPO_RESOL_OFICIO_ACTA_OBSERVADA, N_CODIGO_OFICIO_ACTA_OBSERVADA);
    }


    public static Map<Integer, Integer> getMapTipoResolucionCargoEntrega() {
        return MAP_TIPO_RESOLUCION_CARGO_ENTREGA;
    }


    public static Map<Integer, String> getMapTiposResoluciones() {
        return MAP_TIPOS_RESOLUCIONES;
    }

    public static final Long ID_TABLA_MAE_PROCESO_ELECTORAL = 1L;



    public static final Integer CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_ESCRUTINIO = 1;
    public static final Integer CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_INSTALACION_SUFRAGIO = 2;
    public static final Integer CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_INSTALACION = 3;
    public static final Integer CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_SUFRAGIO = 4;
    public static final Integer CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_RESOLUCION = 5;
    
}
