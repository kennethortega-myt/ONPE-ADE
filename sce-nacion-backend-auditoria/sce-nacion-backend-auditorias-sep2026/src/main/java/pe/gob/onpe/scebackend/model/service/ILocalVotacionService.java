package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.LocalVotacionDto;

public interface ILocalVotacionService {

	List<LocalVotacionDto> findByUbigeo(Long id);
	
}
