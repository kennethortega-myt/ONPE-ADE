package pe.gob.onpe.scebackend.model.orc.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.TabRefreshToken;

public interface TabRefreshTokenRepository extends JpaRepository<TabRefreshToken, Long> {

    Optional<TabRefreshToken> findByUsuario(String usuario);

    Optional<TabRefreshToken> findByToken(String token);
}
