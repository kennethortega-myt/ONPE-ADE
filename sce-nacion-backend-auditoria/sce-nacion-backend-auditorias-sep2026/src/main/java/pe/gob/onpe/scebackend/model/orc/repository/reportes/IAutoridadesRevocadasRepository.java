package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.sql.SQLException;
import java.util.List;

import pe.gob.onpe.scebackend.model.dto.request.reporte.AutoridadesRevocadasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.AutoridadesRevocadasResponseDto;

public interface IAutoridadesRevocadasRepository {

	public List<AutoridadesRevocadasResponseDto> listaAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro) throws SQLException;
}
