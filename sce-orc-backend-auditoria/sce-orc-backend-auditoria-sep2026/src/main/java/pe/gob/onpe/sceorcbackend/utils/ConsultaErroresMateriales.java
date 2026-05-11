package pe.gob.onpe.sceorcbackend.utils;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;

import java.util.Objects;

@Slf4j
public class ConsultaErroresMateriales {

    private ConsultaErroresMateriales() {

    }

    public static String getErrMatANivelDeActa(Acta cabActa, long totalVotosAgrupacionesPoliticas) {

        String errores = "";

        //Total de Ciudadanos que Votaron es mayor que Total de Electores Hábiles
        if (cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null && cabActa.getElectoresHabiles() != null && cabActa.getCvas() > cabActa.getElectoresHabiles()) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_A+ConstantesComunes.SEPARADOR_ERRORES);
        }

        //Total de Votos es mayor Electores Hábiles. B
        if (cabActa.getElectoresHabiles() != null && totalVotosAgrupacionesPoliticas > cabActa.getElectoresHabiles()) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_B+ConstantesComunes.SEPARADOR_ERRORES);
        }

        //Ciudadanos que votaron en el acta de sufragio mayor que Total de Votos, y ambos menores que Electores Hábiles.	D
        if (cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null && cabActa.getElectoresHabiles() != null && cabActa.getCvas() > totalVotosAgrupacionesPoliticas && (cabActa.getCvas() <= cabActa.getElectoresHabiles() && totalVotosAgrupacionesPoliticas <= cabActa.getElectoresHabiles())) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_D+ConstantesComunes.SEPARADOR_ERRORES);
        }

        //Ciudadanos que votaron en el acta de sufragio menor que Total de Votos, y ambos menores que Electores Hábiles.	E
        if (cabActa.getCvas() != null &&
                cabActa.getIlegibleCvas() == null &&
                cabActa.getElectoresHabiles() != null &&
                cabActa.getCvas() < totalVotosAgrupacionesPoliticas &&
                (cabActa.getCvas() <= cabActa.getElectoresHabiles() &&
                totalVotosAgrupacionesPoliticas <= cabActa.getElectoresHabiles())
        ) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_E+ConstantesComunes.SEPARADOR_ERRORES);
        }

        if (!errores.isEmpty() && errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores = errores.substring(0, errores.length() - 1);
        }

        return errores.isEmpty() ? null : errores;

    }


    public static String getErrMatANivelDeActaRevocatoria(Acta cabActa, long totalVotosCalculados) {
        StringBuilder errores = new StringBuilder();

        //A total de ciudadanos que votaron es mayor que el total de electores hábiles
        verificarErrorRevocatoriaA(cabActa, errores);

        //B Total de votos mayor que Total de Electores Hábiles
        verificarErrorRevocatoriaB(cabActa, totalVotosCalculados, errores);

        //Total de votos es mayor que el Total de Ciudadanos que Votaron y ambas menores al Total de Electores Hábiles
        verificarErrorRevocatoriaD(cabActa, totalVotosCalculados, errores);

        //Total de votos es mayor que el Total de Ciudadanos que Votaron y ambas menores al Total de Electores Hábiles
        verificarErrorRevocatoriaE(cabActa, totalVotosCalculados, errores);

        if (errores.isEmpty()) {
            return null;
        }

        if (errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores.deleteCharAt(errores.length() - 1);
        }

        return errores.toString();

    }


    public static String getDetErrorMaterialAgrupol(Acta cabActa, DetActa detActa, long totalVotosPrefPorAgrupacion) {

        StringBuilder errores = new StringBuilder();

        // Votos para una agrupación política, mayor que los Electores Hábiles. C
        verificarErrorC(cabActa, detActa, errores);

        // Votación consignada a favor de una determinada organización política es mayor que el total de ciudadanos que votaron. F
        verificarErrorF(cabActa, detActa, errores);

        // Suma total de votos Preferenciales es mayor al doble de la votación de la misma organización política. K
        verificarErrorK(detActa, totalVotosPrefPorAgrupacion, errores);

        verificarErrorPIlegibleAgrupol(detActa, errores);

        //Suma Total de Votos Preferenciales de los candidatos de una organización política es mayor a la votación de la misma organización política
        if(cabActa.getUbigeoEleccion().getEleccion().getCodigo().equals(ConstantesComunes.COD_ELEC_SENADO_MULTIPLE)){
            verificarErrorM(detActa, totalVotosPrefPorAgrupacion, errores);
        }

        if (errores.isEmpty()) {
            return null;
        }

        if (errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores.deleteCharAt(errores.length() - 1);
        }

        return errores.toString();
    }


    public static String getDetErrorMaterialDetOpcion(Acta acta, long totalVotosPorAutoridad, long totalVotosBNI) {
        //CACA
        StringBuilder errores = new StringBuilder();

        //Total de votos nor Autoridad o Consulta es mayor que electores habilres
        verificarErrorRevocatoriaC(acta, totalVotosPorAutoridad, errores);

        //Total de votos por Autoridad o Consulta es mayor que los Sufragantes Calculados
        verificarErrorRevocatoriaN(errores, totalVotosPorAutoridad, acta.getTotalVotos());

        //Total de votos por Autoridad o Consulta es menor que los Ciudadanos que votaron
        verificarErrorRevocatoriaR(acta, totalVotosPorAutoridad, errores);

        //Total de votos nor Autoridad o Consulta es mayor CHIP los Ciudadanos nue votaron
        verificarErrorRevocatoriaS(acta, totalVotosPorAutoridad, errores);

        //La suma de los votos Blancos, Nulos e Impugnados es mayor que los Electores Hábiles
        verificarErrorRevocatoriaV(acta, totalVotosBNI, errores);

        //La suma de los votos Blancos, Nulos e Impugnados es mayor que los Ciudadanos que votaron
        verificarErrorRevocatoriaW(acta, totalVotosBNI, errores);

        //Total de votos por Autoridad o Consulta es menor que Sufragantes Calculados
        verificarErrorRevocatoriaY(errores, totalVotosPorAutoridad, acta.getTotalVotos());

        if (errores.isEmpty()) {
            return null;
        }

        if (errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores.deleteCharAt(errores.length() - 1);
        }

        return errores.toString();

    }


    private static void verificarErrorRevocatoriaA(Acta cabActa, StringBuilder errores) {

        if (cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null && cabActa.getElectoresHabiles() != null && cabActa.getCvas() > cabActa.getElectoresHabiles()) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_A+ConstantesComunes.SEPARADOR_ERRORES);
        }

    }

    private static void verificarErrorRevocatoriaB(Acta acta, long totalVotosCalculados, StringBuilder errores) {

        if (acta.getElectoresHabiles() != null && totalVotosCalculados > acta.getElectoresHabiles()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_B+ConstantesComunes.SEPARADOR_ERRORES);
            }


    }

    private static void verificarErrorRevocatoriaD(Acta acta, long totalVotosCalculados, StringBuilder errores) {

        if (acta.getElectoresHabiles() != null && acta.getCvas() != null && acta.getIlegibleCvas() == null && totalVotosCalculados < acta.getCvas() && totalVotosCalculados < acta.getElectoresHabiles() &&
                acta.getCvas() < acta.getElectoresHabiles() ) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_D+ConstantesComunes.SEPARADOR_ERRORES);
            }


    }

    private static void verificarErrorRevocatoriaE(Acta acta, long totalVotosCalculados, StringBuilder errores) {

        if (acta.getElectoresHabiles() != null && acta.getCvas() != null && acta.getIlegibleCvas() == null &&
                totalVotosCalculados > acta.getCvas() && totalVotosCalculados < acta.getElectoresHabiles() &&
                acta.getCvas() < acta.getElectoresHabiles() ) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_E+ConstantesComunes.SEPARADOR_ERRORES);
            }


    }


    private static void verificarErrorRevocatoriaV(Acta acta, long totalVotosBNI, StringBuilder errores) {

        if (acta.getElectoresHabiles() != null && totalVotosBNI > acta.getElectoresHabiles()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_V+ConstantesComunes.SEPARADOR_ERRORES);
            }


    }


    private static void verificarErrorRevocatoriaW(Acta acta, long totalVotosBNI, StringBuilder errores) {

        if (acta.getCvas() != null && acta.getIlegibleCvas() == null && totalVotosBNI > acta.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_W+ConstantesComunes.SEPARADOR_ERRORES);
            }


    }


    private static void verificarErrorRevocatoriaN(StringBuilder errores, long totalVotosPorAutoridad, long totalVotosCalculados) {

        if (totalVotosPorAutoridad > totalVotosCalculados) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_N_ILEGIBLE_PREFERENCIAL +ConstantesComunes.SEPARADOR_ERRORES);
        }

    }

    private static void verificarErrorRevocatoriaY(StringBuilder errores, long totalVotosPorAutoridad, long totalVotosCalculados) {
        if (totalVotosPorAutoridad < totalVotosCalculados) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_Y+ConstantesComunes.SEPARADOR_ERRORES);
        }
    }


    private static void verificarErrorRevocatoriaR(Acta cabActa, long totalVotosPorAutoridad, StringBuilder errores) {
        if (cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null && totalVotosPorAutoridad < cabActa.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_R+ConstantesComunes.SEPARADOR_ERRORES);
            }

    }

    private static void verificarErrorRevocatoriaS(Acta cabActa, long totalVotosPorAutoridad, StringBuilder errores) {
        if (cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null && totalVotosPorAutoridad > cabActa.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_S+ConstantesComunes.SEPARADOR_ERRORES);
            }

    }



    private static void verificarErrorC(Acta cabActa, DetActa detActa, StringBuilder errores) {
        if (detActa.getIlegible() == null && cabActa.getElectoresHabiles() != null) {
            long votoAgrupol = detActa.getVotos() == null ? 0L : detActa.getVotos();
            if (votoAgrupol > cabActa.getElectoresHabiles()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_C+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorF(Acta cabActa, DetActa detActa, StringBuilder errores) {
        if (detActa.getIlegible() == null && cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null) {
            long votoAgrupol = detActa.getVotos() == null ? 0L : detActa.getVotos();
            if (votoAgrupol > cabActa.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_F+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorPIlegibleAgrupol(DetActa detActa, StringBuilder errores) {
        if (detActa.getIlegible() != null && detActa.getIlegible().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_P_ILEGIBLE_AGRUPOL+ConstantesComunes.SEPARADOR_ERRORES);
        }
    }

    private static void verificarErrorK(DetActa detActa, long totalVotosPrefPorAgrupacion, StringBuilder errores) {
        if (detActa.getIlegible() == null) {
            long votoAgrupol = detActa.getVotos() == null ? 0L : detActa.getVotos();
            if (totalVotosPrefPorAgrupacion > 2 * votoAgrupol) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_K+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorM(DetActa detActa, long totalVotosPrefPorAgrupacion, StringBuilder errores) {
        if (detActa.getIlegible() == null) {
            long votoAgrupol = detActa.getVotos() == null ? 0L : detActa.getVotos();
            if (totalVotosPrefPorAgrupacion > votoAgrupol) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_M+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    //POSTGRESSS

    public static String getDetErrorMaterialPreferencial(Acta acta, DetActa detActa,
                                                         DetActaPreferencial detActaPreferencial) {

        StringBuilder errores = new StringBuilder();

        // Verificar los diferentes errores materiales
        verificarErrorG(detActa, detActaPreferencial, errores);
        verificarErrorH(acta, detActaPreferencial, errores);
        verificarErrorI(acta, detActaPreferencial, errores);
        verificarErrorJ(detActaPreferencial, acta.getTotalVotos(), errores);
        verificarErrorNIlegiblePreferencial(detActaPreferencial, errores);

        if (errores.isEmpty()) {
            return null;
        }

        if (errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores.deleteCharAt(errores.length() - 1);
        }

        return errores.toString();
    }

    public static String getDetErrorMaterialOpcion(Acta cabActa, DetActaOpcion detActaOpcion) {

        StringBuilder errores = new StringBuilder();

        verificarErrorRevocatoriaC(cabActa, detActaOpcion, errores);
        verificarErrorRevocatoriaF(cabActa, detActaOpcion, errores);
        verificarErrorMaterialImpugnadoVotoOpcion(detActaOpcion, errores);

        if (errores.isEmpty()) {
            return null;
        }

        if (errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores.deleteCharAt(errores.length() - 1);
        }

        return errores.toString();
    }

    /*Votacion consignada a favor de una determinada autoridad o consulta es mayor que el total de electores habiles*/
    private static void verificarErrorRevocatoriaC(Acta cabActa, DetActaOpcion detActaOpcion, StringBuilder errores) {
        if (detActaOpcion.getIlegible() == null && cabActa.getElectoresHabiles() != null) {
            long votoPreferencial = detActaOpcion.getVotos() == null ? 0L : detActaOpcion.getVotos();
            if (votoPreferencial > cabActa.getElectoresHabiles()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_C+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorRevocatoriaC(Acta cabActa, Long totalVotosPorAutoridad, StringBuilder errores) {
        if (totalVotosPorAutoridad != null && cabActa.getElectoresHabiles() != null && totalVotosPorAutoridad > cabActa.getElectoresHabiles()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_C+ConstantesComunes.SEPARADOR_ERRORES);
            }

    }


    private static void verificarErrorRevocatoriaF(Acta cabActa, DetActaOpcion detActaOpcion, StringBuilder errores) {
        if (detActaOpcion.getIlegible() == null && cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null) {
            long votoPreferencial = detActaOpcion.getVotos() == null ? 0L : detActaOpcion.getVotos();
            if (votoPreferencial > cabActa.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_F+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }




    ///// para postgress
    private static void verificarErrorG(DetActa detActa,
                                        DetActaPreferencial detActaPreferencial, StringBuilder errores) {
        if (detActa.getIlegible() == null || detActaPreferencial.getIlegible() == null) {
            long votoAgrupol = detActa.getVotos() == null ? 0L : detActa.getVotos();
            long votoPreferencial = detActaPreferencial.getVotos() == null ? 0L : detActaPreferencial.getVotos();
            if (votoPreferencial > votoAgrupol) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_G+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorH(Acta cabActa, pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial detActaPreferencial, StringBuilder errores) {
        if (detActaPreferencial.getIlegible() == null && cabActa.getCvas() != null && cabActa.getIlegibleCvas() == null) {
            long votoPreferencial = detActaPreferencial.getVotos() == null ? 0L : detActaPreferencial.getVotos();
            if (votoPreferencial > cabActa.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_H+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorI(Acta cabActa, DetActaPreferencial detActaPreferencial, StringBuilder errores) {
        if (detActaPreferencial.getIlegible() == null && cabActa.getElectoresHabiles() != null) {
            long votoPreferencial = detActaPreferencial.getVotos() == null ? 0L : detActaPreferencial.getVotos();
            if (votoPreferencial > cabActa.getElectoresHabiles()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorJ(pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial detActaPreferencial,
                                        long totalVotalAgrupaciones, StringBuilder errores) {
        if (detActaPreferencial.getIlegible() == null) {
            long votoPreferencial = detActaPreferencial.getVotos() == null ? 0L : detActaPreferencial.getVotos();
            if (votoPreferencial > totalVotalAgrupaciones) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_J+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorNIlegiblePreferencial(pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial detActaPreferencial, StringBuilder errores) {
        if (detActaPreferencial.getIlegible() != null && detActaPreferencial.getIlegible().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_N_ILEGIBLE_PREFERENCIAL +ConstantesComunes.SEPARADOR_ERRORES);
        }
    }

    private static void verificarErrorMaterialImpugnadoVotoOpcion(DetActaOpcion detActaOpcion, StringBuilder errores) {

        if (Objects.equals(detActaOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS) &&
            detActaOpcion.getVotos() != null && detActaOpcion.getVotos() > 0) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I_REV+ConstantesComunes.SEPARADOR_ERRORES);
        }

        if (Objects.equals(detActaOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS) &&
            detActaOpcion.getIlegible()!=null && detActaOpcion.getIlegible().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I_REV+ConstantesComunes.SEPARADOR_ERRORES);
        }
    }


}
