package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.MiembroMesaEscrutinioDTO;
import pe.gob.onpe.sceorcbackend.model.dto.RegistroMiembroMesaEscrutinioDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;

public interface MiembroMesaEscrutinioService extends CrudService<MiembroMesaEscrutinioDTO> {

  void save(MiembroMesaEscrutinioDTO miembroMesaEscrutinioDTO, TokenInfo tokenInfo);

  SearchFilterResponse<MiembroMesaEscrutinioDTO> listPaginted(String numeroDocumento, Integer page, Integer size);

  GenericResponse<RegistroMiembroMesaEscrutinioDTO> getRandomMesa(String usuario, Long idproceso, Integer tipoFiltro, boolean reprocesar);

  GenericResponse<PadronDto> consultaPadronPorDni(String dni, TokenInfo tokenInfo, Integer mesaId, boolean primeraConsultaR);


}
