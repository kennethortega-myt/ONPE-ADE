package pe.gob.onpe.scebackend.model.service.impl.comun;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.AmbitoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.comun.IAmbitoElectoralService;

import java.util.List;

@Service
public class AmbitoElectoralService implements IAmbitoElectoralService {

  private final AmbitoElectoralRepository ambitoElectoralRepository;

    public AmbitoElectoralService(AmbitoElectoralRepository ambitoElectoralRepository) {
        this.ambitoElectoralRepository = ambitoElectoralRepository;
    }

    @Override
  public void save(AmbitoElectoral k) {
    this.ambitoElectoralRepository.save(k);
  }

  @Override
  public void saveAll(List<AmbitoElectoral> k) {
    this.ambitoElectoralRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.ambitoElectoralRepository.deleteAll();
  }

  @Override
  @Transactional("locationTransactionManager")
  public List<AmbitoElectoral> findAll() {
    return this.ambitoElectoralRepository.findAll();
  }

  @Override
  @Transactional("locationTransactionManager")
  public AmbitoElectoral getById(Long id) {
    return this.ambitoElectoralRepository.findById(id).orElse(null);
  }

  @Override
  @Transactional("locationTransactionManager")
  public AmbitoElectoral getPadreNacion() {
    try {
      return this.ambitoElectoralRepository.findByAmbitoElectoralPadreIsNull();
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }

  }

}
