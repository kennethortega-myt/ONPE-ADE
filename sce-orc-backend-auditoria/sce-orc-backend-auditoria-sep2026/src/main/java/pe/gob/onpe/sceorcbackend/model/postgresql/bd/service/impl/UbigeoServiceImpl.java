package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.dto.UbigeoDTO;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.NivelUbigeoDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UbigeoEleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UbigeoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UbigeoService;

import org.springframework.stereotype.Service;

@Service
public class UbigeoServiceImpl implements UbigeoService {

  private final UbigeoRepository ubigeoRepository;

  private final UbigeoEleccionRepository ubigeoEleccionRepository;

  public UbigeoServiceImpl(UbigeoRepository ubigeoRepository, UbigeoEleccionRepository ubigeoEleccionRepository) {
    this.ubigeoRepository = ubigeoRepository;
    this.ubigeoEleccionRepository = ubigeoEleccionRepository;
  }

  @Override
  public UbigeoDTO obtenerJerarquiaUbigeo(String codigoUbigeo) {
    List<Object[]> results = ubigeoRepository.getUbigeoGeneral(codigoUbigeo);

    if (results.isEmpty() || results.get(0)[0] == null) {
      throw new BadRequestException("No se encontró información para el ubigeo: " + codigoUbigeo);
    }
    Object[] row = results.get(0);
    return new UbigeoDTO(
        (String) row[0],
        (String) row[1],
        (String) row[2],
        (String) row[3]
    );
  }

  @Override
  public Optional<String> findCodigoAmbitoByCodigoCentroComputo(String codigoCentroComputo) {
    return this.ubigeoRepository.findCodigoAmbitoByCodigoCentroComputo(codigoCentroComputo);
  }

  @Override
  public List<NivelUbigeoDto> buscarDepartamentos() {
    return buscarPorPadre(0L);
  }

  @Override
  public List<NivelUbigeoDto> buscarProvincias(Long idPadreDepartamento) {
    return buscarPorPadre(idPadreDepartamento);
  }

  @Override
  public List<NivelUbigeoDto> buscaDistritos(Long idPadreProvincia) {
    return buscarPorPadre(idPadreProvincia);
  }

  @Override
  public List<EleccionDto> buscaEleccionesPorUbigeo(Long idUbigeo) {
    return this.ubigeoEleccionRepository.findEleccionesByUbigeoId(idUbigeo);
  }

  private List<NivelUbigeoDto> buscarPorPadre(Long idPadre) {
    return this.ubigeoRepository.findByUbigeoPadre_Id(idPadre)
        .stream()
        .map(this::toNivelUbigeoDto)
        .toList();
  }

  private NivelUbigeoDto toNivelUbigeoDto(Ubigeo ubigeo) {
    NivelUbigeoDto dto = new NivelUbigeoDto();
    dto.setId(ubigeo.getId());
    dto.setNombre(ubigeo.getNombre());
    dto.setCodigo(ubigeo.getCodigo());
    return dto;
  }

}
