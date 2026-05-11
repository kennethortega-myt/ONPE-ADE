package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.DetalleConfigRequestDTO;
import pe.gob.onpe.scebackend.model.dto.response.DetalleConfiguracionDocumentoElectoralResponseDTO;

import java.util.List;

public interface IDetalleConfiguracionDocumentoElectoralService {

    void guardarDetalleConfiguracion(DetalleConfigRequestDTO detalles,String usuario);

    List<DetalleConfiguracionDocumentoElectoralResponseDTO> obtenerDetalleByDetalleTipoEleccion(Integer detalleTipoEleccion);

    List<DetalleConfiguracionDocumentoElectoralResponseDTO> obtenerDetalleByDetalleTipoEleccionPaso3(Integer detalleTipoEleccion);
}
