package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.TransmisionEnvio;

import java.util.List;

public interface TransmisionEnvioRepository extends JpaRepository<TransmisionEnvio, Long> {

    List<TransmisionEnvio> findByIdActa(Long idActa);

}
