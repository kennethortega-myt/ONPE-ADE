package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DatosGeneralesResponseDto;
import pe.gob.onpe.scebackend.model.mapper.ITipoEleccionMapper;
import pe.gob.onpe.scebackend.model.repository.TipoEleccionRepository;
import pe.gob.onpe.scebackend.model.service.ITipoEleccionService;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class TipoEleccionServiceImpl implements ITipoEleccionService {

    private final ITipoEleccionMapper tipoEleccionMapper;

    private final TipoEleccionRepository tipoEleccionRepository;


    @Transactional("tenantTransactionManager")
    @Override
    public void save(DatosGeneralesRequestDto datosGeneralesRequestDto) {
        if (Objects.isNull(datosGeneralesRequestDto.getId())) {
            this.tipoEleccionRepository.save(this.tipoEleccionMapper.dtoToTipoEleccion(datosGeneralesRequestDto));
        } else {
            this.tipoEleccionRepository.updateObject(datosGeneralesRequestDto.getNombre(), datosGeneralesRequestDto.getUsuario(),
                    datosGeneralesRequestDto.getId());
        }
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<DatosGeneralesResponseDto> listAll() {
        return this.tipoEleccionRepository.findByActivoOrderByOrdenAsc(SceConstantes.ACTIVO).stream().filter(x->Objects.isNull(x.getIdPadre()))
                .map(this.tipoEleccionMapper::tipoEleccionToDTO).map(dat->{
                    dat.setNombre(dat.getNombre().toLowerCase());
                    return dat;
                }).sorted(Comparator.comparing(DatosGeneralesResponseDto::getNombre)).toList();
    }

    @Transactional("tenantTransactionManager")
    @Override
    public void updateStatus(Integer status, Integer id) {
        this.tipoEleccionRepository.updateEstado(status, id);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public List<DatosGeneralesResponseDto> listAllTipoEleccionHijo(Integer idPadre) {
        return this.tipoEleccionRepository.findByIdPadreOrderByOrdenAsc(idPadre).stream()
                .map(this.tipoEleccionMapper::tipoEleccionToDTO).map(dat->{
                    dat.setNombre(dat.getNombre().toLowerCase());
                    return dat;
                }).sorted(Comparator.comparing(DatosGeneralesResponseDto::getNombre)).toList();
    }
}
