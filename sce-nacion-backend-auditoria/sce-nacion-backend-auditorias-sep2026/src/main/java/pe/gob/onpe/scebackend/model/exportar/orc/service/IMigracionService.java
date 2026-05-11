package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

public interface IMigracionService<Dto, Type> {

	public List<Dto> findByCc(Type ccId);
	
}
