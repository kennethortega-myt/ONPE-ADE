package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionResponseDto;
import pe.gob.onpe.sceorcbackend.model.mapper.IEleccionMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.EleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import org.springframework.stereotype.Service;

@Service
public class EleccionServiceImpl implements EleccionService {

  private final EleccionRepository eleccionRepository;

  private final IEleccionMapper eleccionMapper;

  public EleccionServiceImpl(EleccionRepository eleccionRepository, IEleccionMapper eleccionMapper) {
    this.eleccionRepository = eleccionRepository;
    this.eleccionMapper = eleccionMapper;
  }

  @Override
  public void save(Eleccion eleccion) {
    this.eleccionRepository.save(eleccion);
  }

  @Override
  public void saveAll(List<Eleccion> k) {
    this.eleccionRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.eleccionRepository.deleteAll();
  }

  @Override
  public List<Eleccion> findAll() {
    return this.eleccionRepository.findAll();
  }

  @Override
  public List<Eleccion> findEleccionesByProceso(Long idProceso) {
    return this.eleccionRepository.findByProcesoElectoralIdOrderByCodigoAsc(idProceso);
  }

  @Override
  public List<EleccionResponseDto> findEleccionesByProceso2(Long idProceso) {
    return this.eleccionRepository.findByProcesoElectoralIdOrderByCodigoAsc(idProceso).stream().map(eleccionMapper::entityToDTO).toList();
  }

  @Override
  public Eleccion obtenerEleccionPrincipalPorProceso(Long idProceso) {
    try {
      Optional<Eleccion> eleccion =
          this.eleccionRepository.findByProcesoElectoralIdAndPrincipal(idProceso, SceConstantes.ELECCION_PRINCIPAL);
      return eleccion.orElse(null);
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }
}
