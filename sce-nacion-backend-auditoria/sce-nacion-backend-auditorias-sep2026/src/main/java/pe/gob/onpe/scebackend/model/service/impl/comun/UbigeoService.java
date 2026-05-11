package pe.gob.onpe.scebackend.model.service.impl.comun;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.comun.UbigeoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.NivelUbigeoDto;
import pe.gob.onpe.scebackend.model.orc.repository.comun.IUbigeoNacionRepository;
import pe.gob.onpe.scebackend.model.service.comun.IUbigeoService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UbigeoService implements IUbigeoService {


    private final IUbigeoNacionRepository ubigeoNacionRepository;

    @Override
    @Transactional("locationTransactionManager")
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorCentroComputo(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeUnoPorCentroComputo(filtro);
    }

    @Override
    @Transactional("locationTransactionManager")
    public List<NivelUbigeoDto> listarNivelUbigeUnoPorAmbitoElectoral(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeUnoPorAmbitoElectoral(filtro);
    }

    @Override
    @Transactional("locationTransactionManager")
    public List<NivelUbigeoDto> listarNivelUbigeDosPorNivelUno(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeDosPorNivelUno(filtro);
    }

    @Override
    @Transactional("locationTransactionManager")
    public List<NivelUbigeoDto> listarNivelUbigeTresPorNivelDos(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeTresPorNivelDos(filtro);
    }
    
    @Override
    @Transactional("locationTransactionManager")
    public List<NivelUbigeoDto> listarNivelUbigeUnoDistritoElecXambito(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeUnoDistritoElecXambito(filtro);
    }
    
    @Override
    @Transactional("locationTransactionManager")
    public List<NivelUbigeoDto> listarNivelUbigeDosPorDistritoElec(UbigeoRequestDto filtro) {
        return ubigeoNacionRepository.listarNivelUbigeDosPorDistritoElec(filtro);
    }
}
