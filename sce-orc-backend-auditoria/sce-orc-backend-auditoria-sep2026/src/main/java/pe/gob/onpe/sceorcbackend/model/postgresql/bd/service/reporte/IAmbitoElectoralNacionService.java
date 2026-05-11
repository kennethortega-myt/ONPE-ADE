package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;



import pe.gob.onpe.sceorcbackend.model.dto.reporte.AmbitoElectoralRequestDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AmbitoElectoralDto;

import java.util.List;

public interface IAmbitoElectoralNacionService {
    List<AmbitoElectoralDto> listarAmbitoElectoralPorCentroComputo(AmbitoElectoralRequestDto filtro);
    List<AmbitoElectoralDto> listarAmbitoElectoralPorEleccion(AmbitoElectoralRequestDto filtro);
}
