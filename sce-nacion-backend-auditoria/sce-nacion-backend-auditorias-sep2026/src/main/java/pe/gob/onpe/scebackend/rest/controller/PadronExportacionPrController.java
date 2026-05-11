package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.PaginaDto;
import pe.gob.onpe.scebackend.model.dto.PaginaOptDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.PadronElectoralExportOrcDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.PadronElectoralExportPrDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IPadronElectoralExportService;

@RestController
@RequestMapping("/padron-electoral/exportacion")
public class PadronExportacionPrController {

	private final IPadronElectoralExportService padronService;

    public PadronExportacionPrController(IPadronElectoralExportService padronService) {
        this.padronService = padronService;
    }

    @GetMapping("/pr")
    public PaginaDto<PadronElectoralExportPrDto> importarPadronPr(
            @RequestParam(defaultValue = "0") int numeroPagina,
            @RequestParam(defaultValue = "10") int tamanoPagina) {
        return padronService.importarPadronesPr(numeroPagina, tamanoPagina);
    }
	
	@GetMapping("/orc/{centroComputo}")
    public PaginaDto<PadronElectoralExportOrcDto> importarPadronOrc(
    		@PathVariable("centroComputo") String cc,
    		@RequestParam(defaultValue = "0") int numeroPagina,
            @RequestParam(defaultValue = "10") int tamanoPagina) {
		return padronService.importarPadronesOrc(cc, numeroPagina, tamanoPagina);
	}
	
	@GetMapping("/orc/opt/{centroComputo}")
    public PaginaOptDto<PadronElectoralExportOrcDto> importarOptPadronOrc(
    		@PathVariable("centroComputo") String cc,
    		@RequestParam(defaultValue = "0") Long lastId,
            @RequestParam(defaultValue = "10") int tamanoPagina) {
		return padronService.importarOpPadronesOrc(cc, lastId, tamanoPagina);
	}
	
	@GetMapping("/orc/{centroComputo}/count")
    public Integer countPadronOrc(
    		@PathVariable("centroComputo") String cc) {
		return padronService.contabilizar(cc);
	}
	
}
