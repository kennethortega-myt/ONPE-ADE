package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabCcResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CabCcResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabCcResolucionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CabCcResolucionServiceImpl implements CabCcResolucionService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final CabCcResolucionRepository cabCcResolucionRepository;

  public CabCcResolucionServiceImpl(CabCcResolucionRepository cabCcResolucionRepository){
    this.cabCcResolucionRepository = cabCcResolucionRepository;

  }

  @Override
  public void save(CabCcResolucion cabCcResolucion) {
    this.cabCcResolucionRepository.save(cabCcResolucion);
  }

  @Override
  public void saveAll(List<CabCcResolucion> k) {
    this.cabCcResolucionRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.cabCcResolucionRepository.deleteAll();
  }

  @Override
  public List<CabCcResolucion> findAll() {
    return cabCcResolucionRepository.findAll();
  }

  @Override
  public void deleteAllInBatch() {
    this.cabCcResolucionRepository.deleteAllInBatch();

  }

  @Override
  public Optional<CabCcResolucion> findByActaAndResolucionAndEstadoCambioAndActivo(Long idActa, Long idResolucion, String estadoCambio, Integer activo) {
    return this.cabCcResolucionRepository.findByActaAndResolucionAndEstadoCambioAndActivo(idActa, idResolucion, estadoCambio, activo);
  }

  @Override
  public void spRegistrarCcResolucion(String esquema, Long idActa, String estadoCambio, Long idResolucion, String usuario) {

    Integer resultado=0;
    String mensaje="";

    Map<String, Object> out = this.cabCcResolucionRepository.spRegistrarCcResolucion(esquema, idActa, estadoCambio, idResolucion, usuario, resultado, mensaje);

    resultado = (Integer) out.get("po_resultado");
    mensaje = (String) out.get("po_mensaje");

    if (resultado == null) {
      throw new IllegalStateException("El procedimiento no devolvió resultado.");
    }

    if (resultado == -1) {
      throw new BadRequestException(
          mensaje != null ? mensaje : "Ocurrió un error en el procedimiento."
      );
    }

    if (resultado == 0 || resultado == 1) {
      logger.info("Mensaje del procedimiento: {}", mensaje);
    }
  }
}
