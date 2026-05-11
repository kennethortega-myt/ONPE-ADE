package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.EleccionRequestDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.EleccionDto;

import java.util.List;

public interface IEleccionCustomService {
    List<EleccionDto> obtenerEleccionPorProcesoElectoralId(EleccionRequestDto filtro);

}
