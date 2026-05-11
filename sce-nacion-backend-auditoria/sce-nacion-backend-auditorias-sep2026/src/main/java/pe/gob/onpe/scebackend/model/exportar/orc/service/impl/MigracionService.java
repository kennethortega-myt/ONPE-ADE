package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IMigracionService;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;


public abstract class MigracionService<Dto, Entity, Type> implements IMigracionService<Dto, Type> {

	public List<Dto> findByCc(Type ccId) {
		List<Entity> entities = this.getRepository().findByCc(ccId);
		List<Dto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.getMapper().toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
	public abstract MigracionRepository<Entity, Type> getRepository();
	
	public abstract IMigracionMapper<Dto, Entity>  getMapper();

}
