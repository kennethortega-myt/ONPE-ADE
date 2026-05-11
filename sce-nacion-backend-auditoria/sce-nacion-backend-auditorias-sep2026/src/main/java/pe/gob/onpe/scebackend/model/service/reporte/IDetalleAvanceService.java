package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroDetalleAvanceDto;

import java.sql.SQLException;

public interface IDetalleAvanceService {
    byte[] reporteMiembrosMesaEscrutinio(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException;
    byte[] reportePersoneros(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException;
}
