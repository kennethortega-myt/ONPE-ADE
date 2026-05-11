package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.sql.SQLException;
import java.util.List;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteRecepcionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteRecepcionResponseDto;

public interface IRecepcionRepository {

	public List<ReporteRecepcionResponseDto> listarRecepcion(ReporteRecepcionRequestDto filtro) throws SQLException;
}
