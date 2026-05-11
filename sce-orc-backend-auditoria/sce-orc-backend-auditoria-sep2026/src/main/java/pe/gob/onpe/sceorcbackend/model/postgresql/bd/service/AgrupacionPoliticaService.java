package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.ComboResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;

public interface AgrupacionPoliticaService extends CrudService<AgrupacionPolitica>{

  Optional<AgrupacionPolitica> findById(Long id);

  List<AgrupacionPolitica> findAllById(List<Long> ids);

  List<ComboResponse> listCombo();
  
  Map<String, Object> cargarCandidatos(String esquema, Integer resultado, String mensaje);
}
