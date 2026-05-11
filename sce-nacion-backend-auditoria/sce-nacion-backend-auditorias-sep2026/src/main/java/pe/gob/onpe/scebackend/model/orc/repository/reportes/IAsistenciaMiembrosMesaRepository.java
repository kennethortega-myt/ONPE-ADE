package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.sql.SQLException;
import java.util.List;

import pe.gob.onpe.scebackend.model.dto.request.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.AsistenciaMiembroMesaResponseDto;

public interface IAsistenciaMiembrosMesaRepository {

	public List<AsistenciaMiembroMesaResponseDto> listaAsistenciaMM(AsistenciaMiembroMesaRequestDto filtro) throws SQLException;

}
