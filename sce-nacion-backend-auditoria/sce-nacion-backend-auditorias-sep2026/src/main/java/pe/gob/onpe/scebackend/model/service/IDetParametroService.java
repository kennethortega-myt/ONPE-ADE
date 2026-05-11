package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.DetParametroDto;

import java.util.List;

public interface IDetParametroService extends CrudService<DetParametroDto> {

    List<DetParametroDto> listDetalleByParametro(Long idParametro);
    void actualizarEstado(Integer activo, String usuario,Long idParametro);
    void actualizarValor(String valor, String usuario, String nombre);
}
