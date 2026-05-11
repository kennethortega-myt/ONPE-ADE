package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.scebackend.model.orc.entities.AccesoPc;
import pe.gob.onpe.scebackend.model.orc.repository.comun.IAccesoPcRepositoryCustom;

import java.util.List;

public interface AccesoPcRepository extends JpaRepository<AccesoPc, Long>, IAccesoPcRepositoryCustom {
    boolean existsByIpAccesoPcAndActivo(String ipAccesoPc, Integer activo);
    List<AccesoPc> findAllByActivoOrderByFechaAccesoPcDesc(Integer activo);
    Page<AccesoPc> findAllByActivoOrderByFechaAccesoPcDesc(Integer activo, Pageable pageable);
    List<AccesoPc> findByUsuarioAccesoPcAndActivoOrderByFechaAccesoPcDesc(String usuarioAccesoPc, Integer activo);
}
