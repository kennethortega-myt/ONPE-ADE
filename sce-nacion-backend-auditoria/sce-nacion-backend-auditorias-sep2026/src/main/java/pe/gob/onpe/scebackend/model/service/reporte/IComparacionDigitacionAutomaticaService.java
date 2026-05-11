package pe.gob.onpe.scebackend.model.service.reporte;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroComparacionDigitacionAutomaticaDto;

public interface IComparacionDigitacionAutomaticaService {
    byte[] getComparacionDigiAutoma(FiltroComparacionDigitacionAutomaticaDto filtro);
}
