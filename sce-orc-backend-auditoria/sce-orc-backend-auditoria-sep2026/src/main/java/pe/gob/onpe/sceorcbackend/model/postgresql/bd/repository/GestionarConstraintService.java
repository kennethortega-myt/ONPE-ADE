package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


public interface GestionarConstraintService {

	void eliminarConstraintMiembroMesaSorteado();
	void crearConstraintMiembroMesaSorteado();
	void eliminarConstraintMiembroMesaCola();
	void crearConstraintMiembroMesaCola();
	void crearConstraintOmisoVotante();
	void eliminarConstraintOmisoVotante();
	
}
