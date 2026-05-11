package pe.gob.onpe.scebackend.model.repository;

import java.util.List;


public interface MigracionRepository<T, E>  {

	List<T> findByCc(E ccId);
	
}
