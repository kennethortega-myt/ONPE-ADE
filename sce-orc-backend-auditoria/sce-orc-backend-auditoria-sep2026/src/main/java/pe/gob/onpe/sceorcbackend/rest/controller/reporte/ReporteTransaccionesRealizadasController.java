package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import java.sql.SQLException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTransaccionesRealizadasRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ListaUsuariosReporteTransaccionesDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteTransaccionesRealizadasService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-transacciones-realizadas")
@Log4j2
public class ReporteTransaccionesRealizadasController extends BaseController{

	private final IReporteTransaccionesRealizadasService reporteTransaccionesRealizadasService;
	
	public ReporteTransaccionesRealizadasController(IReporteTransaccionesRealizadasService reporteTransaccionesRealizadasService) {
		this.reporteTransaccionesRealizadasService = reporteTransaccionesRealizadasService;
	}

    @PostMapping("/listaUsuarios")
    public ResponseEntity<GenericResponse<List<ListaUsuariosReporteTransaccionesDTO>>> listaUsuarios(){
    	List<ListaUsuariosReporteTransaccionesDTO> listaUsuarios = reporteTransaccionesRealizadasService.listaUsuarios();
    	
        GenericResponse<List<ListaUsuariosReporteTransaccionesDTO>> genericResponse = new GenericResponse<>();

        if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(listaUsuarios);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
        }

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getTransaccionesRealizadasPdf(@Valid @RequestBody ReporteTransaccionesRealizadasRequestDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuarioVisualizacion(user.getUsuario());
        byte[] resultado = this.reporteTransaccionesRealizadasService.reporte(filtro, authorization);
        
        return getPdfResponse(resultado);
    }
}
