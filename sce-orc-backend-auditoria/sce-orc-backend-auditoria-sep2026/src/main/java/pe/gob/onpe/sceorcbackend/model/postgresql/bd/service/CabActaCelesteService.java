package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationRejectMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationSummaryResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.DigitizationListActasItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ActaScanProjection;

public interface CabActaCelesteService extends CrudService<ActaCeleste> {

	DigitizationSummaryResponse summaryCeleste(String codigoEleccion);
	
	List<DigitizationListActasItem> listActasCeleste(String codigoEleccion, String usuario, String status, int offset, int limit);
	
	void approveMesa(DigitizationApproveMesaRequest request, String usuario, String proceso, String cc);
	
	void rejectActa(String electionId, DigitizationRejectMesaRequest request, TokenInfo tokenInfo);
	
	Optional<ActaCeleste> findById(Long id);

    List<ActaScanProjection> listActasCelesteSceScanner(String codigoEleccion, String estadoDigitalizacion);
}
