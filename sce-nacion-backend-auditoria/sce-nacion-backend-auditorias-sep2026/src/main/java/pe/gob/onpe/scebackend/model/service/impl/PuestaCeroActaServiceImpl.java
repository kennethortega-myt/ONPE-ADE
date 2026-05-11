package pe.gob.onpe.scebackend.model.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.model.dto.MesaComboDto;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroActaDto;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.Mesa;
import pe.gob.onpe.scebackend.model.orc.projections.PuestaCeraActaProjection;
import pe.gob.onpe.scebackend.model.orc.repository.PuestaCeroActaRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.PuestaCeroActaService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Slf4j
@Service
@RequiredArgsConstructor
public class PuestaCeroActaServiceImpl implements PuestaCeroActaService {

    private final CentroComputoService centroComputoService;

    private final PuestaCeroActaRepository puestaCeroactaRepository;

    private final MesaService mesaService;

    private final ITabLogTransaccionalService logService;

    @Override
    public Optional<MesaComboDto> buscarMesaPuestaCeroActa(String schema, String codMesa) {
        Mesa mesa = this.mesaService.findByCodigo(codMesa);
        if (mesa == null) {
            return Optional.empty();
        }
        var mesaDto = MesaComboDto.builder().id(mesa.getId()).mesa(mesa.getCodigo()).build();
        return Optional.of(mesaDto);
    }

    @Override
    @Transactional(value = "locationTransactionManager")
    public void procesarPuestaCeroActa(String schema, PuestaCeroActaDto data, String userAud) {
        final CentroComputo ccNacion = centroComputoService.getPadreNacion();
        final var codigosMesa = data.getMesas().stream().map(PuestaCeroActaDto.Mesa::getCodigo).toList();
        final var mesas = this.mesaService.findByCodigoIn(codigosMesa);

        if (mesas.size() != data.getMesas().size()) {
            var mesasFaltantes = this.getCodigosMesasFaltantes(codigosMesa, mesas);
            String msg = "No se encontraron registros de las mesas: " + String.join(", ", mesasFaltantes);
            throw new IllegalArgumentException(msg);
        }
        final var actas = puestaCeroactaRepository.buscarActasPorMesas(mesas.stream().map(Mesa::getId).toList());
        for (PuestaCeroActaDto.Mesa mesa : data.getMesas()) {
            final var optMesaEntity = mesas.stream().filter(m -> m.getCodigo().equals(mesa.getCodigo())).findFirst();
            if (optMesaEntity.isEmpty()) {
                throw new IllegalArgumentException("No se encontró la mesa " + mesa.getCodigo());
            }
            for (Integer piEleccion : mesa.getElecciones()) {
                log.info(schema);
                log.info(mesa.getCodigo());
                log.info(piEleccion.toString());
                log.info(userAud);
                puestaCeroactaRepository.puestaCeroActa(schema, mesa.getCodigo(), piEleccion,
                        userAud);
            }
            this.registrarLogProcesarPuestaCeroActa(optMesaEntity.get(), mesa, actas, userAud, ccNacion.getCodigo());
        }
    }

    private List<String> getCodigosMesasFaltantes(List<String> codigosMesa, List<Mesa> mesasEncontradas) {
        Set<String> codigosEncontrados = mesasEncontradas.stream()
                .map(Mesa::getCodigo)
                .collect(Collectors.toSet());
        return codigosMesa.stream()
                .filter(c -> !codigosEncontrados.contains(c))
                .toList();
    }

    private void registrarLogProcesarPuestaCeroActa(Mesa mesaEntity, PuestaCeroActaDto.Mesa mesa,
            List<PuestaCeraActaProjection> actas,
            String userAud, String ccNacion) {
        String codigosElecciones = String.join(", ", mesa.getElecciones().stream().map(Object::toString).toList());
        String actasIds = String.join(", ",
                actas.stream()
                        .filter(a -> a.getNMesa().equals(mesaEntity.getId())
                                && mesa.getElecciones().contains(a.getNEleccion().intValue()))
                        .map(a -> a.getNActaPk().toString()).toList());
        String logMsg = String.format("El usuario %s realizó reinicio de acta de la mesa %s, actas %s, elecciones: %s",
                userAud, mesa.getCodigo(), actasIds, codigosElecciones);
        log.info(logMsg);
        logService.registrarLog(
                userAud,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getSimpleName(),
                logMsg,
                "",
                ccNacion,
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                ConstantesComunes.LOG_TRANSACCIONES_ACCION);
    }

}
