package pe.gob.onpe.sceorcbackend.rest.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetProgresoActualDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.ProgresoActualDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportImportadorProgreso;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.ImportImportadorProgresoService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesImportador;

@RestController
@RequestMapping("importador-progreso")
public class ImportadorProgresoController {

	Logger logger = LoggerFactory.getLogger(ImportadorProgresoController.class);
	
	private final ImportImportadorProgresoService importadorService;
	
	
	public ImportadorProgresoController(
			ImportImportadorProgresoService importadorService
			){
		this.importadorService = importadorService;
	}
	
	@GetMapping("/actual")
	public ResponseEntity<GenericResponse<ProgresoActualDto>> getEstadoActual() {
		GenericResponse<ProgresoActualDto> genericResponse = new GenericResponse<>();
		Optional<ImportImportadorProgreso> importadorOp = this.importadorService.findTopByOrderByFechaCreacionDesc();
		ProgresoActualDto progreso = new ProgresoActualDto();
		if(importadorOp.isPresent()){
			ImportImportadorProgreso importador = importadorOp.get();
			progreso.setEstado(importador.getEstado());
			progreso.setPorcentaje(importador.getPorcentaje());
			
			List<DetProgresoActualDto> detallesDto = importador.getDetalle().stream()
                    .map(entity -> {
                    	DetProgresoActualDto detalleDto = new DetProgresoActualDto();
                    	detalleDto.setPorcentaje(entity.getPorcentaje());
                    	detalleDto.setMensaje(entity.getMensaje());
                        return detalleDto;
                    })
                    .toList();
			
			progreso.setDetalles(detallesDto);
			
		} else {
			progreso.setEstado(ConstantesImportador.SIN_IMPORTACION);
		}
		genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(progreso);
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}
	
}
