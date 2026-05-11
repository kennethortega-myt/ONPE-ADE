package pe.gob.onpe.sceorcbackend.model.stae.service;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.stae.dto.ActaElectoralRequestDto;
import pe.gob.onpe.sceorcbackend.model.stae.dto.DocumentoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.stae.dto.DocumentoElectoralRequest;
import pe.gob.onpe.sceorcbackend.model.stae.dto.ResultadoPs;


public interface StaeService {

	ResultadoPs  insertActaStae(
			String piEsquema,
			boolean esDesarollo,
			String piActa,
			String usuario
	);
	
	ResultadoPs  insertListaElectoresStae(
			String piEsquema,
			boolean esDesarollo,
			String piLe,
			String usuario
	);
	
	void guardarDocumentosElectorales(DocumentoElectoralRequest request, String usuario);
	
	boolean validarTokenStae(String tokenBearer, String numeroMesa);
	
	void sendProcessActaStae(
			Acta acta,
			String codUsuario,
			String codCentroComput
			);
	
	void guardarDocumentosElectorales(Long idActa, List<DocumentoElectoralDto> documentos, String usuario);
	
	Optional<Acta> getActa(ActaElectoralRequestDto actaDto);

}
