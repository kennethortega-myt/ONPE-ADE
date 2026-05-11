package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabParametro;

public interface ParametroService extends CrudService<ParametroDto> {

  ParametroDto obtenerParametro(String parametro);

  SearchFilterResponse<CabParametro> listPaginted(TokenInfo tokenInfo, String parametro, Integer page, Integer size);

}
