package pe.gob.onpe.scebackend.model.orc.repository.comun;

import pe.gob.onpe.scebackend.model.dto.request.comun.UbigeoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.NivelUbigeoDto;

import java.util.List;

public interface IUbigeoNacionRepository {
    List<NivelUbigeoDto> listarNivelUbigeUnoPorCentroComputo(UbigeoRequestDto filtro);
    List<NivelUbigeoDto> listarNivelUbigeUnoPorAmbitoElectoral(UbigeoRequestDto filtro);
    List<NivelUbigeoDto> listarNivelUbigeDosPorNivelUno(UbigeoRequestDto filtro);
    List<NivelUbigeoDto> listarNivelUbigeTresPorNivelDos(UbigeoRequestDto filtro);
    List<NivelUbigeoDto> listarNivelUbigeUnoDistritoElecXambito(UbigeoRequestDto filtro);
    List<NivelUbigeoDto> listarNivelUbigeDosPorDistritoElec(UbigeoRequestDto filtro);
}
