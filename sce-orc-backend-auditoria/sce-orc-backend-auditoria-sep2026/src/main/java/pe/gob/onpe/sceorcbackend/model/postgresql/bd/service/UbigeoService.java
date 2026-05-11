package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.dto.UbigeoDTO;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.NivelUbigeoDto;

import java.util.List;
import java.util.Optional;

public interface UbigeoService {
  UbigeoDTO obtenerJerarquiaUbigeo(String codigoUbigeo);

  Optional<String> findCodigoAmbitoByCodigoCentroComputo(String codigoCentroComputo);

  List<NivelUbigeoDto> buscarDepartamentos();

  List<NivelUbigeoDto> buscarProvincias(Long idPadre);

  List<NivelUbigeoDto> buscaDistritos(Long idPadre);

  List<EleccionDto> buscaEleccionesPorUbigeo(Long idUbigeo);

}
