package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.LocalVotacionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.LocalVotacion;

import java.util.List;

public interface LocalVotacionService extends CrudService<LocalVotacion> {

   List<LocalVotacionDTO> listarLocalesPorUbigeo(Long idUbigeo);

}
