package pe.gob.onpe.sceorcbackend.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.DetActaOficioBean;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaCelesteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaFormatoRepository;

public final class ActaOficioValidator {

    private ActaOficioValidator() {
    }

    public static List<DetActaOficioBean> filtrarActasValidas(List<ActaBean> actaBeans, ActaRepository actaRepository,
            ActaCelesteRepository actaCelesteRepository, DetActaFormatoRepository detActaFormatoRepository) {

        List<DetActaOficioBean> actasValidas = new ArrayList<>();

        for (ActaBean bean : actaBeans) {

            Acta acta = actaRepository.findById(bean.getActaId()).orElse(null);
            if (acta == null)
                continue;

            boolean staeValido = Long.valueOf(2).equals(acta.getSolucionTecnologica())
                    && Integer.valueOf(1).equals(acta.getTipoTransmision());

            boolean estadoValido = ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO
                    .equalsIgnoreCase(acta.getEstadoActa());

            ActaCeleste celeste = actaCelesteRepository.findByActa_Id(acta.getId()).orElse(null);

            boolean celesteValida = staeValido
                    || (celeste != null && ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA
                            .equalsIgnoreCase(celeste.getEstadoDigitalizacion()));

            boolean cargoGenerado = detActaFormatoRepository.findByActa_Id(acta.getId()).stream()
                    .anyMatch(daf -> daf.getCabActaFormato() != null && daf.getCabActaFormato().getFormato() != null
                            && Objects.equals(daf.getCabActaFormato().getFormato().getTipoFormato(),
                                    ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE)
                            && Objects.equals(daf.getActivo(), ConstantesComunes.ACTIVO));

            if (estadoValido && celesteValida && cargoGenerado) {

                DetActaOficioBean dto = new DetActaOficioBean();
                dto.setActaPlomo(acta);
                dto.setActaCeleste(celeste);
                dto.setCabActaFormato(detActaFormatoRepository.findByActa_Id(acta.getId()).stream().findFirst()
                        .map(DetActaFormato::getCabActaFormato).orElse(null));

                actasValidas.add(dto);
            }
        }

        return actasValidas;
    }
}
