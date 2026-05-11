package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.DetParametroDto;

public interface DetParametroService extends CrudService<DetParametroDto> {

  List<DetParametroDto> listDetalleByParametro(Long idParametro);

  void actualizarEstado(Integer activo, String usuario,Long idParametro);

}
