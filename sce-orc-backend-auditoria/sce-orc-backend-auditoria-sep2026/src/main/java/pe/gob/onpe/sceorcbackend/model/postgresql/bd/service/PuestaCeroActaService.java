package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.dto.MesaDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.PuestaCeroActaDto;

public interface PuestaCeroActaService {

    Optional<MesaDTO> buscarMesaPuestaCeroActa(String codMesa);

    void procesarPuestaCeroActa(PuestaCeroActaDto data, TokenInfo tokenInfo);

}
