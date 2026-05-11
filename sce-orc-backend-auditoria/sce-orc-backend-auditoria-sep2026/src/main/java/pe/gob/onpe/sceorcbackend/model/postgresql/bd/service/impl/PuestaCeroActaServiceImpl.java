package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import pe.gob.onpe.sceorcbackend.model.dto.MesaDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.PuestaCeroActaDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.PuestaCeroActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.PuestaCeraActaProjection;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

@Slf4j
@Service
@RequiredArgsConstructor
public class PuestaCeroActaServiceImpl implements PuestaCeroActaService {

    private final PuestaCeroActaRepository puestaCeroactaRepository;

    private final MesaService mesaService;

    private final ITabLogService logService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @Override
    public Optional<MesaDTO> buscarMesaPuestaCeroActa(String codMesa) {
        Mesa mesa = this.mesaService.findByCodigo(codMesa);
        if (mesa == null) {
            return Optional.empty();
        }
        var mesaDto = MesaDTO.builder().id(mesa.getId()).mesa(mesa.getCodigo()).build();
        return Optional.of(mesaDto);
    }

    @Override
    @Transactional
    public void procesarPuestaCeroActa(PuestaCeroActaDto data, TokenInfo tokenInfo) {
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
                puestaCeroactaRepository.puestaCeroActa(schema, mesa.getCodigo(), piEleccion,
                        tokenInfo.getNombreUsuario());
            }
            this.registrarLogProcesarPuestaCeroActa(optMesaEntity.get(), mesa, actas, tokenInfo);
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

    private void registrarLogProcesarPuestaCeroActa(Mesa mesaEntity, PuestaCeroActaDto.Mesa mesa, List<PuestaCeraActaProjection> actas,
            TokenInfo tokenInfo) {
        String codigosElecciones = String.join(", ", mesa.getElecciones().stream().map(Object::toString).toList());
        String actasIds = String.join(", ",
                actas.stream()
                        .filter(a -> a.getNMesa().equals(mesaEntity.getId())
                                && mesa.getElecciones().contains(a.getNEleccion().intValue()))
                        .map(a -> a.getNActaPk().toString()).toList());
        String logMsg = String.format("El usuario %s realizó el reinicio de acta de la mesa %s, actas %s, elecciones: %s",
                tokenInfo.getNombreUsuario(), mesa.getCodigo(), actasIds, codigosElecciones);
        log.info(logMsg);
        logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                logMsg,
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                ConstantesComunes.LOG_TRANSACCIONES_ACCION);
    }

}
