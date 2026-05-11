package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores.VerificationLe;

public interface VerificationServiceListaElectores {

    GenericResponse<VerificationLe> getRandomListaElectores(TokenInfo tokenInfo, boolean reprocesar);

    GenericResponse<Boolean> saveListaElectores(VerificationLe request, TokenInfo tokenInfo, boolean reprocesar);

    GenericResponse<Boolean> rechazarListaElectores(Long mesaId, TokenInfo tokenInfo);

}
