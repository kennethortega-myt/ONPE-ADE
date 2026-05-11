package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.FiltroAvanceMesaDto;

public interface AvanceMesaMesaService {

    byte[] reporteAvanceMesa(FiltroAvanceMesaDto filtro) throws JRException;

}
