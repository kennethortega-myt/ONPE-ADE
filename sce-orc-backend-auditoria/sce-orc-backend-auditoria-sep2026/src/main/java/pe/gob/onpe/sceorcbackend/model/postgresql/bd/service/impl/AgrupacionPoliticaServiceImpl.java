package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.sceorcbackend.model.dto.ComboResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.AgrupacionPoliticaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AgrupacionPoliticaService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgrupacionPoliticaServiceImpl implements AgrupacionPoliticaService {

  private final AgrupacionPoliticaRepository agrupacionPoliticaRepository;

  public AgrupacionPoliticaServiceImpl(AgrupacionPoliticaRepository agrupacionPoliticaRepository) {
    this.agrupacionPoliticaRepository = agrupacionPoliticaRepository;
  }

  @Override
  public void save(AgrupacionPolitica agrupacionPolitica) {
    this.agrupacionPoliticaRepository.save(agrupacionPolitica);
  }

  @Override
  public void saveAll(List<AgrupacionPolitica> k) {
    this.agrupacionPoliticaRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.agrupacionPoliticaRepository.deleteAll();
  }

  @Override
  public List<AgrupacionPolitica> findAll() {
    return this.agrupacionPoliticaRepository.findAll();
  }

  @Override
  public Optional<AgrupacionPolitica> findById(Long id) {
    return this.agrupacionPoliticaRepository.findById(id);
  }

  @Override
  public List<AgrupacionPolitica> findAllById(List<Long> ids) {
    return this.agrupacionPoliticaRepository.findAllById(ids);
  }

  @Override
  public List<ComboResponse> listCombo() {
    return this.agrupacionPoliticaRepository.findByTipoAgrupacionPoliticaOrderByDescripcionAsc(1L).stream().map(op->{
      ComboResponse comboResponse = new ComboResponse();
      comboResponse.setId(op.getId());
      comboResponse.setDescripcion(op.getDescripcion());
      return comboResponse;
    }).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public Map<String, Object> cargarCandidatos(String esquema, Integer resultado, String mensaje) {
	  return this.agrupacionPoliticaRepository.cargarCandidatos(esquema, resultado, mensaje);
  }
}
