package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.ParametroDto;
import pe.gob.onpe.scebackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.scebackend.model.orc.entities.CabParametro;

public interface IParametroService extends CrudService<ParametroDto> {

    ParametroDto obtenerParametro(String parametro);

    SearchFilterResponse<CabParametro> listPaginted(String parametro, Integer page, Integer size);
}
