package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.AreaDTO;
import pe.gob.onpe.scebackend.model.dto.request.CoordenadasRequestDTO;

import java.util.List;

public interface IExternalApiService {

    List<AreaDTO> computeRelativeCoordinates(CoordenadasRequestDTO request);
}
