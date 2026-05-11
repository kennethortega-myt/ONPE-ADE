package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveModelo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;

public interface ModeloService {

    GenericResponse<Boolean> approveMesaModelo(DigitizationApproveModelo request);
}
