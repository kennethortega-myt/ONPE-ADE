package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabRefreshToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TabRefreshTokenRepository extends JpaRepository<TabRefreshToken, Long> {

    Optional<TabRefreshToken> findByUsuario(String usuario);

    Optional<TabRefreshToken> findByToken(String token);
}
