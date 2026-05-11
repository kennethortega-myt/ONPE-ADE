package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.JuradoElectoralEspecialDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;

public interface JuradoElectoralService extends CrudService<JuradoElectoralEspecialDto> {

  SearchFilterResponse<JuradoElectoralEspecialDto> listPaginted(String filter, Integer page, Integer size);

}
