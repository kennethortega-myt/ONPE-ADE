package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DatosGeneralesResponseDto;

import java.util.List;

public interface ITipoEleccionService extends IGenericService<DatosGeneralesRequestDto, DatosGeneralesResponseDto>{

    List<DatosGeneralesResponseDto> listAllTipoEleccionHijo(Integer idPadre);
}
