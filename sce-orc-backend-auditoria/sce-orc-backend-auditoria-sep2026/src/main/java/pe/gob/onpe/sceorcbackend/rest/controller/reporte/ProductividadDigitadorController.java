package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ProductividadDigitadorRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.UsuarioDigitadorResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ProductividadDigitadorService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@RequestMapping("productividad-digitador")
public class ProductividadDigitadorController extends BaseController{

	Logger logger = LoggerFactory.getLogger(ProductividadDigitadorController.class);
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	private final ProductividadDigitadorService productividadDigitadorService;
	
	public ProductividadDigitadorController(ProductividadDigitadorService productividadDigitadorService) {
		this.productividadDigitadorService = productividadDigitadorService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getProductividadDigitadorPdf(@Valid @RequestBody ProductividadDigitadorRequestDto filtro) {
		filtro.setEsquema(schema);
        byte[] resultado = this.productividadDigitadorService.getReporteProductividadDigitador(filtro);
        
        return getPdfResponse(resultado);
    }
	
	@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("usuarios/{idCentroComputo}")
    public ResponseEntity<GenericResponse<List<UsuarioDigitadorResponseDto>>> listUsuariosDigitador(                                                   
                                           @PathVariable("idCentroComputo") Integer idCentroComputo,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                           @RequestHeader("X-Tenant-Id") String tentat
    ) {
		
		List<UsuarioDigitadorResponseDto> listaDigitadores = this.productividadDigitadorService.obtenerListaUsuariosDigitador(schema, idCentroComputo);
		HttpStatus httpStatus = HttpStatus.OK;
        GenericResponse<List<UsuarioDigitadorResponseDto>> genericResponse = new GenericResponse<>();

        if (listaDigitadores != null && !listaDigitadores.isEmpty()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(listaDigitadores);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
        }

        return new ResponseEntity<>(genericResponse, httpStatus);
    }

}
