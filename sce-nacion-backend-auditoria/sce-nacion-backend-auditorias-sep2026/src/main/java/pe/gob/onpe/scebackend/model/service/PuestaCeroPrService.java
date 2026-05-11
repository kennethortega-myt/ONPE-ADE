package pe.gob.onpe.scebackend.model.service;

public interface PuestaCeroPrService {

	boolean puestoCeroPr(String usuarioSce, Long id);
	void registrarPuestaCeroPr(Long id, String usuarioSce,  boolean exitoso);

}
