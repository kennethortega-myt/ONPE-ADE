package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.orc.entities.PadronElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.PadronElectoralRepository;
import pe.gob.onpe.scebackend.model.service.IPadronElectoralService;

@Service
public class PadronElectoralServiceImpl implements IPadronElectoralService {

    private final PadronElectoralRepository padronElectoralRepository;

    public PadronElectoralServiceImpl(PadronElectoralRepository padronElectoralRepository) {
        this.padronElectoralRepository = padronElectoralRepository;
    }


    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public PadronElectoral getPadronElectoral(String documentoIdentidad) {
       try{
           return this.padronElectoralRepository.findByDocumentoIdentidad(documentoIdentidad);
       } catch (Exception e) {
           throw new GenericException(e.getMessage());
       }
    }
}
