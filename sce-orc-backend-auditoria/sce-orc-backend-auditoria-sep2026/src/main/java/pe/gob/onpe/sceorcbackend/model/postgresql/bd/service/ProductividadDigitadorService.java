package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ProductividadDigitadorRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.UsuarioDigitadorResponseDto;

public interface ProductividadDigitadorService  {

	public byte[] getReporteProductividadDigitador(ProductividadDigitadorRequestDto filtro);
	
	public List<UsuarioDigitadorResponseDto> obtenerListaUsuariosDigitador(String esquema, Integer idCentroComputo);
}
