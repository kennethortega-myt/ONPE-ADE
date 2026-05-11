package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.NivelUbigeoDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.UbigeoRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.IUbigeoNacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IUbigeoService;

import java.util.List;

@Service
public class UbigeoService implements IUbigeoService {

    @Autowired
    private IUbigeoNacionRepository ubigeoNacionRepository;

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorCentroComputo(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeUnoPorCentroComputo(filtro);
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorAmbitoElectoral(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeUnoPorAmbitoElectoral(filtro);
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeDosPorNivelUno(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeDosPorNivelUno(filtro);
    }

    @Override
    public List<NivelUbigeoDto> listarNivelUbigeTresPorNivelDos(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeTresPorNivelDos(filtro);
    }
    
    @Override
    public List<NivelUbigeoDto> listarNivelUbigeUnoDistritoElecXambito(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeUnoDistritoElecXambito(filtro);
    }
    
    @Override
    public List<NivelUbigeoDto> listarNivelUbigeDosPorDistritoElec(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeDosPorDistritoElec(filtro);
    }
    
    
}
