package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaFormato;

import java.util.List;

public interface DetActaFormatoRepository extends JpaRepository<DetActaFormato, Long> {

    void deleteDetActaFormatoByActa(Acta acta);

    List<DetActaFormato> findByActa_IdOrderByFechaCreacion(Long idActa);
    
    @Modifying
	@Query("DELETE FROM DetActaFormato")
	void deleteAllInBatch();
    
    List<DetActaFormato> findByActa_Id(Long id);
    
    @Query("SELECT d FROM DetActaFormato d " +
    "JOIN d.cabActaFormato c " +
    "WHERE c.id = ?1")
    List<DetActaFormato> findByIdCabActaFormato(Long id);

}
