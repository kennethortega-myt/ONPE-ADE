package pe.gob.onpe.scebackend.model.service.impl.comun;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.comun.CentroComputoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.CentroComputoDto;
import pe.gob.onpe.scebackend.model.orc.repository.comun.ICentroComputoNacionRepository;
import pe.gob.onpe.scebackend.model.service.comun.ICentroComputoNacionService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CentroComputoNacionNacionService implements ICentroComputoNacionService {


   private final ICentroComputoNacionRepository centroComputoRepository;

    @Override
    @Transactional("locationTransactionManager")
    public List<CentroComputoDto> listarCentroComputo(CentroComputoRequestDto filtro) {
        return centroComputoRepository.listarCentroComputo(filtro);
    }

    @Override
    @Transactional("locationTransactionManager")
    public List<CentroComputoDto> listarCentroComputoPorEleccion(CentroComputoRequestDto filtro) {
        return centroComputoRepository.listarCentroComputoPorEleccion(filtro);
    }
    
    @Override
    @Transactional("locationTransactionManager")
    public List<CentroComputoDto> listarCentroComputoPorAmbitoElectoral(CentroComputoRequestDto filtro) {
        return centroComputoRepository.listarCentroComputoPorAmbitoElectoral(filtro);
    }
}
