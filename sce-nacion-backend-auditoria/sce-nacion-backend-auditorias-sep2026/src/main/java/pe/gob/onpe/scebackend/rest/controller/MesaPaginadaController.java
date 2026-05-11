package pe.gob.onpe.scebackend.rest.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.PaginaOptDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.MesaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IMesaPaginadaExportService;

@RestController
@RequestMapping("/mesa-exportacion")
public class MesaPaginadaController {

	@Autowired
	private IMesaPaginadaExportService mesaService;
	
	@GetMapping("/cc/{cc}")
    public PaginaOptDto<MesaExportDto> importar(
    		@PathVariable String cc,
    		@RequestParam(defaultValue = "0") Long lastId,
            @RequestParam(defaultValue = "10") int tamanoPagina) {
		return mesaService.importarMesas(cc, lastId, tamanoPagina);
	}
	
	@GetMapping("/cc/{cc}/count")
    public Integer contar(
    		@PathVariable String cc) {
		return mesaService.contar(cc);
	}
		
	
}
