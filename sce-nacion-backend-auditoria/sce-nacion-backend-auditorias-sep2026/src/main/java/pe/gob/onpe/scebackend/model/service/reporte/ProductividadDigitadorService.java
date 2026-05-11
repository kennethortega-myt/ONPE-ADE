package pe.gob.onpe.scebackend.model.service.reporte;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ProductividadDigitadorRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.UsuarioDigitadorResponseDto;

public interface ProductividadDigitadorService {

	public byte[] getReporteProductividadDigitador(ProductividadDigitadorRequestDto filtro);
	
	public List<UsuarioDigitadorResponseDto> obtenerListaUsuariosDigitador(String esquema, Integer idCentroComputo);
}
