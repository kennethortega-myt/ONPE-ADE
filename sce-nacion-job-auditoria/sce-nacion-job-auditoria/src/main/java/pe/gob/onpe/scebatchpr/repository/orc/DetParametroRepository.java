package pe.gob.onpe.scebatchpr.repository.orc;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebatchpr.entities.orc.DetParametro;


public interface DetParametroRepository extends JpaRepository<DetParametro, Long> {
    @Query("""
                SELECT d.valor
                FROM DetParametro d
                JOIN d.parametro c
                WHERE c.parametro = :parametro
                  AND c.activo = :activo
                  AND d.activo = :activo
            """)
    Optional<String> findValorParametro(String parametro, Integer activo);
}
