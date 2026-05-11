package pe.gob.onpe.scebackend.model.service.impl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.dto.LocalVotacionDto;
import pe.gob.onpe.scebackend.model.orc.entities.LocalVotacion;
import pe.gob.onpe.scebackend.model.orc.repository.LocalVotacionRepository;
import pe.gob.onpe.scebackend.model.service.ILocalVotacionService;

@Service
public class LocalVotacionService implements ILocalVotacionService {

	@Autowired
	private LocalVotacionRepository localVotacionRepository;
	
	@Override
	public List<LocalVotacionDto> findByUbigeo(Long id) {
		
		List<LocalVotacion> locales = this.localVotacionRepository.findByUbigeo(id);
		List<LocalVotacionDto> localesDto = locales.parallelStream().map(local -> {
										LocalVotacionDto localDto = new LocalVotacionDto();
										localDto.setId(local.getId());
										localDto.setNombre(local.getNombre());
							            return localDto;
							        })
									.collect(Collectors.toList());
		return localesDto;
	}


	

}
