package pe.gob.onpe.sceorcbackend.utils;

import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.verification.BarCodeInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaInfo;

import java.util.Arrays;
import java.util.Objects;

public class ActaUtils {

    private ActaUtils() {

    }

    public static void validarEstadoDigitalizacionActa(ActaInfo actaInfo, Long tipo) {
        if (ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA
                .equals(actaInfo.getActa().getEstadoCc())) {

            switch (actaInfo.getActa().getEstadoActa()) {
                case ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA ->
                        throw new BadRequestException(String.format(
                                "La mesa %s de elección %s no se puede digitalizar, se encuentra contabilizada por extravío.",
                                actaInfo.getBarCodeInfo().getNroMesa(),
                                actaInfo.getNombreEleccion()
                        ));

                case ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA ->
                        throw new BadRequestException(String.format(
                                "La mesa %s, de elección %s, no se puede digitalizar, se encuentra contabilizada por siniestro.",
                                actaInfo.getBarCodeInfo().getNroMesa(),
                                actaInfo.getNombreEleccion()
                        ));

                default ->
                        throw new BadRequestException(String.format(
                                "La mesa %s, de elección %s, no se puede digitalizar, se encuentra contabilizada.",
                                actaInfo.getBarCodeInfo().getNroMesa(),
                                actaInfo.getNombreEleccion()
                        ));
            }
        }

        if (!Arrays.asList(
                ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,
                ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA,
                ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA
        ).contains(actaInfo.getActa().getEstadoDigitalizacion())) {

            throw new BadRequestException(String.format(
                    "El acta %s no se encuentra en estado PENDIENTE o RECHAZADA.",
                    actaInfo.getBarCodeInfo().getCodigoBarra()
            ));
        }

        if (Objects.equals(tipo, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE) && actaInfo.getActa().getArchivoEscrutinio() != null) {
            throw new BadRequestException(String.format("El AE de %s, se encuentra digitalizado.", actaInfo.getBarCodeInfo().getCodigoBarra()));
        }
        if (Objects.equals(tipo, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS) && actaInfo.getActa().getArchivoInstalacionSufragio() != null) {
            throw new BadRequestException(String.format("El AIS de %s, se encuentra digitalizado.", actaInfo.getBarCodeInfo().getCodigoBarra()));
        }
    }


    public static void validarSolucionTecnologica(ActaInfo actaInfo ) {

        if(Objects.equals(actaInfo.getActa().getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_STAE)){
            if(actaInfo.getActa().getTipoTransmision() == null ||
                    !actaInfo.getActa().getTipoTransmision().equals(ConstantesComunes.TIPO_HOJA_STAE_CONTINGENCIA)) {
                throw new BadRequestException(String.format(
                        "La mesa %s de elección %s no se puede digitalizar. Las actas STAE deben habilitarse por contingencia.",
                        actaInfo.getBarCodeInfo().getNroMesa(),
                        actaInfo.getNombreEleccion()
                ));
            }

        }else if(Objects.equals(actaInfo.getActa().getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_VOTO_DIGITAL)){
            throw new BadRequestException(String.format(
                    "La mesa %s de elección %s no se puede digitalizar, al ser de solución tecnologica voto digital.",
                    actaInfo.getBarCodeInfo().getNroMesa(),
                    actaInfo.getNombreEleccion()
            ));

        }
    }

    public static boolean isActaReconteo(final Acta acta){
        return Objects.isNull(acta.getNumeroCopia()) && acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION) &&
                acta.getEstadoCc().equals(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
    }


    public static void validarCopiaEnAeAisParaConvencionalesContingencia(Acta acta, ActaInfo actaInfo) {

        boolean esStaeContingencia =
            Objects.equals(acta.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_STAE)
                && ConstantesComunes.TIPO_HOJA_STAE_CONTINGENCIA.equals(acta.getTipoTransmision());

        boolean esConvencional =
            Objects.equals(acta.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_CONVENCIONAL);

        if ((esStaeContingencia || esConvencional)
            && acta.getNumeroCopia() != null
            && !acta.getNumeroCopia().equals(actaInfo.getBarCodeInfo().getNroCopia())) {

            throw new BadRequestException(String.format(
                "La mesa %s de elección %s no se puede digitalizar, la copia escaneada %s es diferente a lo registrado en el sistema %s.",
                actaInfo.getBarCodeInfo().getNroMesa(),
                actaInfo.getNombreEleccion(),
                actaInfo.getBarCodeInfo().getNroCopia(),
                acta.getNumeroCopia()
            ));
        }
    }
}
