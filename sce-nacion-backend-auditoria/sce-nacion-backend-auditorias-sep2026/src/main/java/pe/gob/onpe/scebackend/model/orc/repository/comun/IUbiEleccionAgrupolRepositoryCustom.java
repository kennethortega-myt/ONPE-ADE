package pe.gob.onpe.scebackend.model.orc.repository.comun;

import pe.gob.onpe.scebackend.model.dto.request.comun.UbiEleccionAgrupolRequestDto;

public interface IUbiEleccionAgrupolRepositoryCustom {
    Integer obtenerCantidadAgrupacionPreferencial(UbiEleccionAgrupolRequestDto filtro);
}
