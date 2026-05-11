package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes;

import java.sql.SQLException;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.AutoridadesRevocadasRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.AutoridadesRevocadasResponseDto;

public interface IAutoridadesRevocadasRepository {

	public List<AutoridadesRevocadasResponseDto> listaAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro) throws SQLException;
}
