package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroAvanceMesaDto;

public interface AvanceMesaMesaService {

	byte[] reporteAvanceMesa(FiltroAvanceMesaDto filtro) throws JRException;
}
