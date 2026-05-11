package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationActaDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.exception.VerificationActaException;

public interface VerificationService {

    GenericResponse<VerificationActaDTO> obtenerActaRandom(String codigoEleccion, TokenInfo tokenInfo) throws VerificationActaException;

    GenericResponse<VerificationActaDTO> obtenerActaRandomConImagenes(String codigoEleccion, TokenInfo tokenInfo) throws VerificationActaException;

    GenericResponse<Boolean> guardar(VerificationActaDTO request, TokenInfo tokenInfo);


    PadronDto consultaPadronPorDni(String dni,String mesa,  TokenInfo tokenInfo, Boolean omitirMesa);
    GenericResponse<VerificationActaDTO> obtenerActaRandomParaProcesamientoManual(String codigoEleccion, TokenInfo tokenInfo) throws VerificationActaException;
}
