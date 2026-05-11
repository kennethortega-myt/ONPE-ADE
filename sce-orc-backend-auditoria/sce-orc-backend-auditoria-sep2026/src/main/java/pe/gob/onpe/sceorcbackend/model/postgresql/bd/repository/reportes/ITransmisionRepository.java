package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes;

import java.sql.SQLException;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteTransmisionResponseDto;

public interface ITransmisionRepository {

	public List<ReporteTransmisionResponseDto> listarTransmision(ReporteTransmisionRequestDto filtro) throws SQLException;
}
