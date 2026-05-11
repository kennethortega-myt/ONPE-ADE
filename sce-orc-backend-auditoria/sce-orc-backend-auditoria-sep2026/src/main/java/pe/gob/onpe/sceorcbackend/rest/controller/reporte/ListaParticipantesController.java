package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ListaParticipantesRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ListaParticipantesService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("lista-participantes")
public class ListaParticipantesController extends BaseController{


	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	private final ListaParticipantesService listaParticipantesService;
	
	public ListaParticipantesController(ListaParticipantesService listaParticipantesService) {
		this.listaParticipantesService = listaParticipantesService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getListaParticipantesPdf(@Valid @RequestBody ListaParticipantesRequestDto filtro) {
        filtro.setEsquema(schema);
        byte[] resultado = this.listaParticipantesService.getReporteListaParticipantes(filtro);
        
        return getPdfResponse(resultado);
    }
}
