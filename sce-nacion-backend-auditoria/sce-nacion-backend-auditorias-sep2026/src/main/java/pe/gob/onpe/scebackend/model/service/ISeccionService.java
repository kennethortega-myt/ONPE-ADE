package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DatosGeneralesResponseDto;

public interface ISeccionService extends IGenericService<DatosGeneralesRequestDto, DatosGeneralesResponseDto>{

     DatosGeneralesResponseDto save2(DatosGeneralesRequestDto seccion);
}
