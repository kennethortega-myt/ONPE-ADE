package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


import pe.gob.onpe.sceorcbackend.model.dto.request.UbiEleccionAgrupolRequestDto;

public interface IUbiEleccionAgrupolRepositoryCustom {
    Integer obtenerCantidadAgrupacionPreferencial(UbiEleccionAgrupolRequestDto filtro);
}
