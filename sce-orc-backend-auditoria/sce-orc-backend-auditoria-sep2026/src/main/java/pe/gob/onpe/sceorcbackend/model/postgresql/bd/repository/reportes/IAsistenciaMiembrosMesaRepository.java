package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes;

import java.sql.SQLException;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.AsistenciaMiembroMesaResponseDto;

public interface IAsistenciaMiembrosMesaRepository {

	public List<AsistenciaMiembroMesaResponseDto> listaAsistenciaMM(AsistenciaMiembroMesaRequestDto filtro) throws SQLException;
}
