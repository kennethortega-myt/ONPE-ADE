package pe.gob.onpe.scebackend.model.stae.utils;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.model.stae.dto.ActaElectoralRequestDto;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaDto;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaPreferencialDto;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesEstadoActa;

@Slf4j
public class ConsultaErroresMateriales {

    private ConsultaErroresMateriales() {

    }

    public static String getErrMatANivelDeActa(ActaElectoralRequestDto actaElectoralRequestDto, Integer totalVotosAgrupacionesPoliticas, Integer cantidadElectoresHabiles) {

        String errores = "";

        if (actaElectoralRequestDto.getCvas() != null && cantidadElectoresHabiles != null && actaElectoralRequestDto.getCvas() > cantidadElectoresHabiles) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_A+ ConstantesComunes.SEPARADOR_ERRORES);
        }

        //Total de Votos es mayor Electores Hábiles. B
        if (cantidadElectoresHabiles != null && totalVotosAgrupacionesPoliticas > cantidadElectoresHabiles) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_B+ConstantesComunes.SEPARADOR_ERRORES);
        }

        //Ciudadanos que votaron en el acta de sufragio mayor que Total de Votos, y ambos menores que Electores Hábiles.	D
        //ok Total de votos es menor que el Total de Ciudadanos que Votaron y Total de Ciudadanos que Votaron menor o igual al Total de Electores Hábiles
        if (actaElectoralRequestDto.getCvas() != null && cantidadElectoresHabiles != null
                && actaElectoralRequestDto.getCvas() > totalVotosAgrupacionesPoliticas
                && (actaElectoralRequestDto.getCvas() <= cantidadElectoresHabiles)) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_D+ConstantesComunes.SEPARADOR_ERRORES);
        }

        //Ciudadanos que votaron en el acta de sufragio menor que Total de Votos, y ambos menores que Electores Hábiles.	E
        //ok Total de votos es mayor que el Total de Ciudadanos que Votaron y Total de Ciudadanos que Votaron menor o igual al Total de Electores Hábiles
        if (actaElectoralRequestDto.getCvas() != null &&
                cantidadElectoresHabiles!= null &&
                actaElectoralRequestDto.getCvas() < totalVotosAgrupacionesPoliticas &&
                (actaElectoralRequestDto.getCvas() <= cantidadElectoresHabiles &&
                        totalVotosAgrupacionesPoliticas <= cantidadElectoresHabiles)
        ) {
            errores = errores.concat(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_E+ConstantesComunes.SEPARADOR_ERRORES);
        }

        if (!errores.isEmpty() && errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores = errores.substring(0, errores.length() - 1);
        }

        return errores.isEmpty() ? null : errores;

    }



    public static String getDetErrorMaterialAgrupol(ActaElectoralRequestDto actaElectoralRequestDto,
                                                    DetalleActaDto detalleActaDto,
                                                    Integer totalVotosPrefPorAgrupacion, String codigoEleccion, Integer cantidadElectoresHabiles) {

        StringBuilder errores = new StringBuilder();

        // Votos para una agrupación política, mayor que los Electores Hábiles. C
        verificarErrorC(detalleActaDto, errores, cantidadElectoresHabiles);

        // Votación consignada a favor de una determinada organización política es mayor que el total de ciudadanos que votaron. F
        verificarErrorF(actaElectoralRequestDto, detalleActaDto, errores);

        // Suma total de votos Preferenciales es mayor al doble de la votación de la misma organización política. K
        verificarErrorK(detalleActaDto, totalVotosPrefPorAgrupacion, errores);

        //Suma Total de Votos Preferenciales de los candidatos de una organización política es mayor a la votación de la misma organización política
        if(codigoEleccion.equals(ConstantesComunes.COD_ELEC_SENADO_MULTIPLE)){
            verificarErrorM(detalleActaDto, totalVotosPrefPorAgrupacion, errores);
        }

        if (errores.isEmpty()) {
            return null;
        }

        if (errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores.deleteCharAt(errores.length() - 1);
        }

        return errores.toString();

    }


    private static void verificarErrorC(DetalleActaDto detalleActaDto,
                                        StringBuilder errores,
                                        Integer cantidadElectoresHabiles) {

        if (cantidadElectoresHabiles == null) {
            return;
        }

        int votoAgrupol = detalleActaDto.getVotos() != null
                ? detalleActaDto.getVotos()
                : 0;

        if (votoAgrupol > cantidadElectoresHabiles) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_C)
                    .append(ConstantesComunes.SEPARADOR_ERRORES);
        }
    }

    private static void verificarErrorF(ActaElectoralRequestDto actaElectoralRequestDto, DetalleActaDto detalleActaDto, StringBuilder errores) {
        if ( actaElectoralRequestDto.getCvas() != null) {
            int votoAgrupol = detalleActaDto.getVotos() == null ? 0 : detalleActaDto.getVotos();
            if (votoAgrupol > actaElectoralRequestDto.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_F+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorK(DetalleActaDto detalleActaDto, Integer totalVotosPrefPorAgrupacion, StringBuilder errores) {
        int votoAgrupol = detalleActaDto.getVotos() == null ? 0 : detalleActaDto.getVotos();
        if (totalVotosPrefPorAgrupacion > 2 * votoAgrupol) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_K+ConstantesComunes.SEPARADOR_ERRORES);
        }
    }

    private static void verificarErrorM(DetalleActaDto detalleActaDto, Integer totalVotosPrefPorAgrupacion, StringBuilder errores) {
        int votoAgrupol = detalleActaDto.getVotos() == null ? 0 : detalleActaDto.getVotos();
        if (totalVotosPrefPorAgrupacion > votoAgrupol) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_M+ConstantesComunes.SEPARADOR_ERRORES);
        }
    }


    public static String getDetErrorMaterialPreferencial(ActaElectoralRequestDto actaElectoralRequestDto,
                                                         DetalleActaDto detalleActaDto,
                                                         DetalleActaPreferencialDto detalleActaPreferencialDto,
                                                         Integer cantidadElectoresHabiles) {

        StringBuilder errores = new StringBuilder();

        // Verificar los diferentes errores materiales
        verificarErrorG(detalleActaDto, detalleActaPreferencialDto, errores);
        verificarErrorH(actaElectoralRequestDto, detalleActaPreferencialDto, errores);
        verificarErrorI(detalleActaPreferencialDto, errores ,cantidadElectoresHabiles);
        verificarErrorJ(detalleActaPreferencialDto, actaElectoralRequestDto.getTotalVotos(), errores);

        if (errores.isEmpty()) {
            return null;
        }

        if (errores.charAt(errores.length() - 1) == ConstantesComunes.SEPARADOR_ERRORES.charAt(0)) {
            errores.deleteCharAt(errores.length() - 1);
        }

        return errores.toString();
    }


    private static void verificarErrorG(DetalleActaDto detalleActaDto, DetalleActaPreferencialDto detalleActaPreferencialDto, StringBuilder errores) {

        int votoAgrupol = detalleActaDto.getVotos() == null ? 0 : detalleActaDto.getVotos();
        int votoPreferencial = detalleActaPreferencialDto.getVotos() == null ? 0 : detalleActaPreferencialDto.getVotos();
        if (votoPreferencial > votoAgrupol) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_G+ConstantesComunes.SEPARADOR_ERRORES);
        }
    }

    private static void verificarErrorH(ActaElectoralRequestDto cabActa, DetalleActaPreferencialDto detalleActaPreferencialDto, StringBuilder errores) {
        if (cabActa.getCvas() != null) {
            int votoPreferencial = detalleActaPreferencialDto.getVotos() == null ? 0 : detalleActaPreferencialDto.getVotos();
            if (votoPreferencial > cabActa.getCvas()) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_H+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorI(DetalleActaPreferencialDto detalleActaPreferencialDto,
                                        StringBuilder errores,
                                        Integer cantidadElectoresHabiles) {
        if (cantidadElectoresHabiles != null) {
            int votoPreferencial = detalleActaPreferencialDto.getVotos() == null ? 0 : detalleActaPreferencialDto.getVotos();
            if (votoPreferencial > cantidadElectoresHabiles) {
                errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I+ConstantesComunes.SEPARADOR_ERRORES);
            }
        }
    }

    private static void verificarErrorJ(DetalleActaPreferencialDto detalleActaPreferencialDto,
                                        Integer totalVotalAgrupaciones, StringBuilder errores) {
        int votoPreferencial = detalleActaPreferencialDto.getVotos() == null ? 0 : detalleActaPreferencialDto.getVotos();
        if (votoPreferencial > totalVotalAgrupaciones) {
            errores.append(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_J+ConstantesComunes.SEPARADOR_ERRORES);
        }
    }

}
