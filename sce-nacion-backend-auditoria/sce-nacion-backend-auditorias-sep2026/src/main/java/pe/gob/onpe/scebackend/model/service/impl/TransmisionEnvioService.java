package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.orc.entities.TransmisionEnvio;
import pe.gob.onpe.scebackend.model.orc.repository.TransmisionEnvioRepository;
import pe.gob.onpe.scebackend.model.service.ITransmisionEnvioService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransmisionEnvioService implements ITransmisionEnvioService {


    private final TransmisionEnvioRepository transmisionEnvioRepository;
    
    @Override
    @Transactional(value = "locationTransactionManager", readOnly = true)
    public List<TransmisionEnvio> listbyIdActa(Long idActa) {
        List<TransmisionEnvio> lista = new ArrayList<>();
        try {
            lista = this.transmisionEnvioRepository.findByIdActa(idActa);
        } catch (Exception e) {
           log.error(e.getMessage());
        }
        return lista;
    }

}
