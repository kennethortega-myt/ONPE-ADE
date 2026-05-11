package pe.gob.onpe.sceorcbackend.utils;

import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;

public class MesaUtils {

    private MesaUtils() {

    }

    public static void validateEstadoDigitalizacionPorTipoDocumento(Mesa mesa, String tipoDocumento) {

        if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_CONTROL_DE_ASISTENCIA_MIEMBROS_MESA) &&
                !mesa.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE) &&
                !mesa.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_RECHAZADA)) {
            throw new BadRequestException(
                    "Solo se permite digitalizar las mesas que se encuentren en estado Pendiente de digitalización y " +
                            "digitalizaciones rechazadas."
            );
        } else if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES) &&
                !mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE) &&
                !mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE) &&
                !mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_RECHAZADA)) {
            throw new BadRequestException(
                    "Solo se permite digitalizar las mesas que se encuentren en estado pendiente de digitalización, digitalización parcial y " +
                            "digitalizaciones rechazadas."
            );
        }
    }
}
