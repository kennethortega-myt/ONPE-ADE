package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.PersoneroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.PersoneroRequestDTO;
import pe.gob.onpe.sceorcbackend.model.dto.RegistroPersoneroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;

public interface PersoneroService extends CrudService<PersoneroDTO> {

  SearchFilterResponse<PersoneroDTO> listPaginted(String numeroDocumento, Integer page, Integer size);

  GenericResponse<RegistroPersoneroDTO> getRandomMesa(String usuario, Long idproceso, Integer tipoFiltro, boolean reprocesar);

  GenericResponse<PadronDto> consultaPadronPorDni(String dni, TokenInfo tokenInfo, Integer mesaId);

  void save(PersoneroDTO personeroDTO, PersoneroRequestDTO request, TokenInfo tokenInfo);

}
