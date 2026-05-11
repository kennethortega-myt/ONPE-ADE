package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa.VerificationMm;

public interface VerificationServiceMiembrosMesa {

    GenericResponse<VerificationMm> getRandomMiembrosMesa(TokenInfo tokenInfo, boolean reprocesar, String tipoDenuncia);

    GenericResponse<Boolean> saveMiembrosMesa(VerificationMm request, TokenInfo tokenInfo, boolean reprocesar);

    GenericResponse<Boolean> rechazarMiembrosMesa(Long mesaId, TokenInfo tokenInfo);

}
