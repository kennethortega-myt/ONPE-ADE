package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.response.DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;

import java.util.List;

public interface IDetalleTipoEleccionDocumentoElectoralEscrutinioService {

     GenericResponse actualizarDocumentoElectoral(List<DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO> lista, String usuario);
}
