package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import java.sql.SQLException;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTransaccionesRealizadasRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ListaUsuariosReporteTransaccionesDTO;

public interface IReporteTransaccionesRealizadasService {
	List<ListaUsuariosReporteTransaccionesDTO> listaUsuarios();
	byte[] reporte(ReporteTransaccionesRealizadasRequestDto filtro, String authorization) throws JRException, SQLException;
}
