package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AccesoPc;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccesoPcRepository extends JpaRepository<AccesoPc, Long> {

    //Busca el primer acceso de un usuario
    Optional<AccesoPc> findFirstByUsuarioAccesoPcOrderByFechaAccesoPcAsc(String usuarioAccesoPc);

    //Verifica si existe algún acceso registrado para el usuario
    boolean existsByUsuarioAccesoPc(String usuarioAccesoPc);

    // Timeout de 5 segundos para no quedarse bloqueado si hay TRUNCATE (puesta a cero)
    @QueryHints(@QueryHint(name = "org.hibernate.timeout", value = "5"))
    boolean existsByIpAccesoPcAndActivo(String ipAccesoPc, Integer activo);
    List<AccesoPc> findAllByActivoOrderByFechaAccesoPcDesc(Integer activo);
    Page<AccesoPc> findAllByActivoOrderByFechaAccesoPcDesc(Integer activo, Pageable pageable);
    List<AccesoPc> findByUsuarioAccesoPcAndActivoOrderByFechaAccesoPcDesc(String usuarioAccesoPc, Integer activo);
}
