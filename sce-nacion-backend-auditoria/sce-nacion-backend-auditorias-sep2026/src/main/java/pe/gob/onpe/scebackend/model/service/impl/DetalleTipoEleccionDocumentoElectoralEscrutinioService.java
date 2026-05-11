package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.response.DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.repository.DetalleTipoEleccionDocumentoElectoralEscrutinioRepository;
import pe.gob.onpe.scebackend.model.service.IDetalleTipoEleccionDocumentoElectoralEscrutinioService;

import java.util.Date;
import java.util.List;

@Service
public class DetalleTipoEleccionDocumentoElectoralEscrutinioService implements IDetalleTipoEleccionDocumentoElectoralEscrutinioService {

    private final DetalleTipoEleccionDocumentoElectoralEscrutinioRepository detalleTipoEleccionDocumentoElectoralEscrutinioRepository;

    public DetalleTipoEleccionDocumentoElectoralEscrutinioService(DetalleTipoEleccionDocumentoElectoralEscrutinioRepository detalleTipoEleccionDocumentoElectoralEscrutinioRepository) {
        this.detalleTipoEleccionDocumentoElectoralEscrutinioRepository = detalleTipoEleccionDocumentoElectoralEscrutinioRepository;
    }


    @Override
    @Transactional("locationTransactionManager")
    public GenericResponse actualizarDocumentoElectoral(List<DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO> lista, String usuario) {
      GenericResponse response = new GenericResponse();
       try{
           lista.forEach(det->
               this.detalleTipoEleccionDocumentoElectoralEscrutinioRepository.updateDocumentoElectoral(det.getDocumentoElectoral(),usuario, new Date(), det.getId().longValue())
           );
           response.setSuccess(Boolean.TRUE);
       }catch (Exception e){
           throw new GenericException(e.getMessage());
       }
       return response;
    }
}
