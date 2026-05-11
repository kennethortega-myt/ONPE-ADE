package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CargoTransmisionNacion;

public interface CargoTransmisionNacionRepository extends JpaRepository<CargoTransmisionNacion, Long> {

	@Query("SELECT a " +
            "FROM CargoTransmisionNacion a " + 
			"WHERE a.estadoTransmitidoNacion = :estadoTransmitidoNacion " + 
            "AND a.accion = :accion " +
			"AND a.idActa = :idActa " +
			"AND a.transmite = 1 " +
			"AND (a.intento is null or a.intento=0) " +
            "AND a.requestCargoTransmision is not null ")
	List<CargoTransmisionNacion> listarFaltantesTransmitir(
			Integer estadoTransmitidoNacion,
			String accion,
			Long idActa);
	
	@Query("SELECT a " +
            "FROM CargoTransmisionNacion a " + 
			"WHERE a.estadoTransmitidoNacion = :estadoTransmitidoNacion " + 
			"AND a.transmite = 1 " +
			"AND a.intento>=1 " + 
            "AND a.requestCargoTransmision is not null ")
	List<CargoTransmisionNacion> listarFaltantesTransmitirPorFallo(
			Integer estadoTransmitidoNacion);
	
}
