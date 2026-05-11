package pe.gob.onpe.scebackend.model.service;

import java.util.Optional;

import pe.gob.onpe.scebackend.model.dto.MesaComboDto;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroActaDto;

public interface PuestaCeroActaService {

    Optional<MesaComboDto> buscarMesaPuestaCeroActa(String schema, String codMesa);

    void procesarPuestaCeroActa(String schema, PuestaCeroActaDto data, String userAud);

}
