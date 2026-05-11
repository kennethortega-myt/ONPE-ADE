package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroDetalleAvanceDto;

import java.sql.SQLException;

public interface IDetalleAvanceService {
    byte[] reporteMiembrosMesaEscrutinio(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException;
    byte[] reportePersoneros(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException;
}
