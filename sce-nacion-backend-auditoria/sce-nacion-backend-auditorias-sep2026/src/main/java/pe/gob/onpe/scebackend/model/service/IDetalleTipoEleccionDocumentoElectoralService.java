package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.DetalleTipoEleccionDocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DetalleTipoEleccionDocumentoElectoralResponseDto;

import java.io.IOException;
import java.util.List;

public interface IDetalleTipoEleccionDocumentoElectoralService {
    
    void guardarDetalle(DetalleTipoEleccionDocumentoElectoralRequestDto detalle,String usuario);
    
    List<DetalleTipoEleccionDocumentoElectoralResponseDto> obtenerDetalleByTipo(Integer tipoEleccion, boolean isConfigGeneral);

    List<DetalleTipoEleccionDocumentoElectoralResponseDto> obtenerDetalleByTipoPaso2(Integer tipoEleccion);

    List<DetalleTipoEleccionDocumentoElectoralResponseDto> obtenerDetalleByTipoPaso2Config();

    void actualizarArchivo(Integer idDetalle, Long idArchivo) throws IOException;
}
