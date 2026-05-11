package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTransaccionesRealizadasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ListaUsuariosReporteTransaccionesDTO;

import java.sql.SQLException;
import java.util.List;

public interface IReporteTransaccionesRealizadasService {
    List<ListaUsuariosReporteTransaccionesDTO> listaUsuarios(ReporteTransaccionesRealizadasRequestDto filtro);
    byte[] reporte(ReporteTransaccionesRealizadasRequestDto filtro, String authorization) throws JRException, SQLException;
}
