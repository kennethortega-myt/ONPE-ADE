package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.ExportarPrDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.ExportarPrRequestDto;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IExportarPrService;

@RestController
@Validated
@RequestMapping("/exportar-pr")
public class ExportarPrController {

	private final IExportarPrService exportarPrService;

    public ExportarPrController(IExportarPrService exportarPrService) {
        this.exportarPrService = exportarPrService;
    }

    @PostMapping("/")
    public ResponseEntity<ExportarPrDto> exportarPr(@RequestBody ExportarPrRequestDto request) {
    	
    	ExportarPrDto rpta = this.exportarPrService.exportar(request);
    	
        return new ResponseEntity<>(rpta, HttpStatus.OK);
    }
	
}
