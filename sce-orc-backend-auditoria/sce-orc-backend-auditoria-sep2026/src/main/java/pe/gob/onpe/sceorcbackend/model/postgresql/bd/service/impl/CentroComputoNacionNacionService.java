package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.CentroComputoRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ICentroComputoNacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.ICentroComputoNacionService;


import java.util.List;

@Service
public class CentroComputoNacionNacionService implements ICentroComputoNacionService {

    @Autowired
    ICentroComputoNacionRepository centroComputoRepository;

    @Override
    public List<CentroComputoDto> listarCentroComputo(CentroComputoRequestDto filtro) {
        return centroComputoRepository.listarCentroComputo(filtro);
    }

    @Override
    public List<CentroComputoDto> listarCentroComputoPorEleccion(CentroComputoRequestDto filtro) {
        return centroComputoRepository.listarCentroComputoPorEleccion(filtro);
    }
    
    @Override
    public List<CentroComputoDto> listarCentroComputoPorAmbitoElectoral(CentroComputoRequestDto filtro) {
        return centroComputoRepository.listarCentroComputoPorAmbitoElectoral(filtro);
    }
}
