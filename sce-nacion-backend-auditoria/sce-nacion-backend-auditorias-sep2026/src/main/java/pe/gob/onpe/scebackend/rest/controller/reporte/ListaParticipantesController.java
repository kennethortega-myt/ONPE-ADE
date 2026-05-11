package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ListaParticipantesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ListaParticipantesService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("lista-participantes")
public class ListaParticipantesController extends BaseController{

	private final ListaParticipantesService listaParticipantesService;
	
	public ListaParticipantesController(TokenDecoder tokenDecoder, ListaParticipantesService listaParticipantesService) {
        super(tokenDecoder);
        this.listaParticipantesService = listaParticipantesService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getListaParticipantesPdf(@Valid @RequestBody ListaParticipantesRequestDto filtro) {
        byte[] resultado = this.listaParticipantesService.getReporteListaParticipantes(filtro);
        
        return getPdfResponse(resultado);
    }
}
