package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoVotante;

public interface OmisoVotanteRepository extends JpaRepository<OmisoVotante, Long> {

    @Modifying
    @Query("DELETE FROM OmisoVotante")
    void deleteAllInBatch();
    
    @Query("SELECT o FROM OmisoVotante o "
    		+ "JOIN o.mesa m "
    		+ "WHERE m.id = ?1 ")
    List<OmisoVotante> findByMesaId(Long idMesa);

    @Query("SELECT o FROM OmisoVotante o "
            + "JOIN o.mesa m "
            + "WHERE o.activo = 1 and m.id = ?1 ")
    List<OmisoVotante> findByMesaIdActivo(Long idMesa);

    @Modifying
    @Transactional
    @Query("UPDATE OmisoVotante o " +
            "SET o.fechaModificacion = CURRENT_TIMESTAMP, o.usuarioModificacion = :usuario, o.activo= 0 where o.mesa.id = :idMesa and o.activo = 1")
    int inhabilitarOmisoVotante(@Param("usuario") String usuario,
                           @Param("idMesa") Long idMesa);


    @Query("SELECT COUNT(p) FROM OmisoVotante p WHERE p.activo = :activo")
    Long contarPorActivo(@Param("activo") Integer activo);
}
