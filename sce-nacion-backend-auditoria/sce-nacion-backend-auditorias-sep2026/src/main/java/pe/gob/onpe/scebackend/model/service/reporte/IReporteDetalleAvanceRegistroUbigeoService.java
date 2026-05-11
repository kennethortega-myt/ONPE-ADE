package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteDetalleAvanceRegistroUbigeoRequestDto;

import java.sql.SQLException;

public interface IReporteDetalleAvanceRegistroUbigeoService {
    byte[] reporte(ReporteDetalleAvanceRegistroUbigeoRequestDto filtro, String authorization) throws JRException, SQLException;
}
