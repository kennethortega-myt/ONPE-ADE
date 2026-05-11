package pe.gob.onpe.scebackend.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.ParametroConexionDto;
import pe.gob.onpe.scebackend.model.dto.ParametroConexionFiltroDto;
import pe.gob.onpe.scebackend.model.dto.ParametroConexionList;
import pe.gob.onpe.scebackend.model.dto.PingConexionDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.service.ICentroComputoService;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
@RestController
@RequestMapping("/parametro-conexion-cc")
public class ParametroConexionCcController {

	private final ICentroComputoService centroComputoService;
	
	public ParametroConexionCcController(ICentroComputoService centroComputoService) {
	    this.centroComputoService = centroComputoService;
	  }

	@PostMapping("/buscar")
	public ResponseEntity<GenericResponse> listAll(@RequestBody ParametroConexionFiltroDto filtro) {
		GenericResponse response = new GenericResponse();
		try {
			List<CentroComputo> ccs = this.centroComputoService.findAll(filtro);
			List<ParametroConexionList> lista = new ArrayList<>();
			for(CentroComputo cc:ccs){
				lista.add(ParametroConexionList.builder()
						.codigoCc(cc.getCodigo())
						.nombre(cc.getNombre())
						.idCentroComputo(cc.getId())
						.puerto(cc.getPuertoBackedCc())
						.ip(cc.getIpBackendCc())
						.protocolo(cc.getProtocolBackendCc())
						.estado((cc.getEstadoConfiguracionCc()!=null && cc.getEstadoConfiguracionCc().equals(ConstantesComunes.ACTIVO)) 
								? "Activo" 
								: "Inactivo")
						.activo(cc.getEstadoConfiguracionCc())
						.esActivo(cc.getEstadoConfiguracionCc()!=null && cc.getEstadoConfiguracionCc().equals(ConstantesComunes.ACTIVO))
						.fechaModificacion(DateUtil.getDateString(cc.getFechaModificacion(), "dd/MM/yyyy HH:mm:ss"))
						.usuarioModificacion(cc.getUsuarioModificacion())
						.build());
			}
			response.setData(lista);
			response.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/ping")
	public ResponseEntity<GenericResponse> ping(@RequestBody ParametroConexionDto dto) {
		GenericResponse response = new GenericResponse();
		try {
			
			PingConexionDto pingConexionDto = PingConexionDto
					.builder()
					.puerto(dto.getPuerto())
					.ip(dto.getIp().trim())
					.protocolo(dto.getProtocolo().trim())
					.build();
			
			boolean exitoso = this.centroComputoService.ping(pingConexionDto);
			response.setSuccess(exitoso);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/")
	public ResponseEntity<GenericResponse> save(@RequestBody ParametroConexionDto dto) {
		GenericResponse response = new GenericResponse();
		try {
			CentroComputo cc = this.centroComputoService.getCentroComputoByPk(dto.getIdCentroComputo());
			String token = this.centroComputoService.generarToken(32);
			if(cc!=null){
				cc.setPuertoBackedCc(dto.getPuerto());
				cc.setIpBackendCc(dto.getIp().trim());
				cc.setProtocolBackendCc(dto.getProtocolo().trim());
				cc.setApiTokenBackedCc(token);
				this.centroComputoService.save(cc);
			}
			response.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/activar")
	public ResponseEntity<GenericResponse> activar(@RequestBody ParametroConexionDto dto) {
		GenericResponse response = new GenericResponse();
		try {
			CentroComputo cc = this.centroComputoService.getCentroComputoByPk(dto.getIdCentroComputo());
			if(cc!=null){
				Integer activo = dto.isActivar() ? ConstantesComunes.ACTIVO : ConstantesComunes.INACTIVO;
				cc.setEstadoConfiguracionCc(activo);
				this.centroComputoService.save(cc);
			}
			response.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
