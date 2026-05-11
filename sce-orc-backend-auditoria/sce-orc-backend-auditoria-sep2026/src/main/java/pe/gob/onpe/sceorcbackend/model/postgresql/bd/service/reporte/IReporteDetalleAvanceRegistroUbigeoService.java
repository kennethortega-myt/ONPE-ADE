package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteDetalleAvanceRegistroUbigeoRequestDto;

import java.sql.SQLException;

public interface IReporteDetalleAvanceRegistroUbigeoService {
    byte[] reporte(ReporteDetalleAvanceRegistroUbigeoRequestDto filtro, String authorization) throws JRException, SQLException;
}
