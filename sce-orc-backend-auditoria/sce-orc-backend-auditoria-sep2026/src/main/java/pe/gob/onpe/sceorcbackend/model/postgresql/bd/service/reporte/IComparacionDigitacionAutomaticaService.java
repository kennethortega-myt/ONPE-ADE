package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroComparacionDigitacionAutomaticaDto;

import java.sql.SQLException;

public interface IComparacionDigitacionAutomaticaService {

    byte[] getComparacionDigiAutoma(FiltroComparacionDigitacionAutomaticaDto filtro) throws JRException, SQLException;
}
