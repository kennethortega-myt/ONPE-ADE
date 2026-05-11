package pe.gob.onpe.scebackend.rest.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportOrcDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportarDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportarRequestDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.impl.ExportarOrcService;
import pe.gob.onpe.scebackend.model.service.ITabInicializacionCentroComputoService;


@RestController
@Validated
@RequestMapping("/exportar")
public class ExportarController {
	
	Logger logger = LoggerFactory.getLogger(ExportarController.class);

    private ExportarOrcService exportarOrcService;

	private ITabInicializacionCentroComputoService tabInicializacionComputoService;
	
	public ExportarController(
		ExportarOrcService exportarOrcService,
		ITabInicializacionCentroComputoService tabInicializacionComputoService){
		this.exportarOrcService = exportarOrcService;
		this.tabInicializacionComputoService = tabInicializacionComputoService;
	}
    
    @PostMapping("/")
    public ResponseEntity<ExportarDto> exportar(@RequestBody ExportarRequestDto request) {
    	logger.info("Inicio de la exportacion");
        this.tabInicializacionComputoService.guardarInicializarCc(request);
        ExportOrcDto exportarOrcDto = this.exportarOrcService.exportar(request.getAcronimo(), request.getCc());
        ExportarDto exportarDto = new ExportarDto();
        exportarDto.setIdCentroComputo(request.getCc());
        exportarDto.setProceso(request.getAcronimo());

        // orc
        exportarDto.setAgrupacionPolitica(exportarOrcDto.getAgrupacionPolitica());
        exportarDto.setAgrupacionPoliticaFicticia(exportarOrcDto.getAgrupacionPoliticaFicticia());
        exportarDto.setAgrupacionPoliticaReal(exportarOrcDto.getAgrupacionPoliticaReal());
        //exportarDto.setActa(exportarOrcDto.getActa());
        exportarDto.setAmbitoElectoral(exportarOrcDto.getAmbitoElectoral());
        exportarDto.setCentroComputo(exportarOrcDto.getCentroComputo());
        exportarDto.setDetActa(exportarOrcDto.getDetActa());
        exportarDto.setDetActaPreferencial(exportarOrcDto.getDetActaPreferencial());
        exportarDto.setUbigeoEleccionAgrupacionPolitica(exportarOrcDto.getUbigeoEleccionAgrupacionPolitica());
        exportarDto.setEleccion(exportarOrcDto.getEleccion());
        exportarDto.setLocalVotacion(exportarOrcDto.getLocalVotacion());
        exportarDto.setUbigeo(exportarOrcDto.getUbigeo());
        //exportarDto.setMesa(exportarOrcDto.getMesa());
        exportarDto.setUbigeoEleccion(exportarOrcDto.getUbigeoEleccion());
        exportarDto.setProcesoElectoral(exportarOrcDto.getProcesoElectoral());
        exportarDto.setCatalogo(exportarOrcDto.getCatalogo());
        exportarDto.setCatalogoEstructura(exportarOrcDto.getCatalogoEstructura());
        exportarDto.setCatalogoReferencia(exportarOrcDto.getCatalogoReferencia());
        exportarDto.setFormatos(exportarOrcDto.getFormatos());
        exportarDto.setCabActasFormatos(exportarOrcDto.getCabActasFormatos());
        exportarDto.setDetActasFormatos(exportarOrcDto.getDetActasFormatos());
        exportarDto.setDistritoElectorales(exportarOrcDto.getDistritoElectorales());
        exportarDto.setCandidatos(exportarOrcDto.getCandidatos());
        exportarDto.setCandidatosFicticios(exportarOrcDto.getCandidatosFicticios());
        exportarDto.setCandidatosReales(exportarOrcDto.getCandidatosReales());
        exportarDto.setUsuario(exportarOrcDto.getUsuario());
        exportarDto.setPuestaCero(exportarOrcDto.getPuestaCero());
        exportarDto.setVersion(exportarOrcDto.getVersion());
        exportarDto.setMiembroMesaSorteado(exportarOrcDto.getMiembroMesaSorteado());
        exportarDto.setDetalleConfiguracionDocumentoElectoral(exportarOrcDto.getDetalleConfiguracionDocumentoElectoral());
        exportarDto.setDetalleTipoEleccionDocumentoElectoral(exportarOrcDto.getDetalleTipoEleccionDocumentoElectoral());
        exportarDto.setSeccion(exportarOrcDto.getSeccion());
        exportarDto.setDocumentoElectoral(exportarOrcDto.getDocumentoElectoral());
        exportarDto.setDetActaOpcion(exportarOrcDto.getDetActaOpcion());
        exportarDto.setOpcionesVoto(exportarOrcDto.getOpcionesVoto());
        exportarDto.setCabParametro(exportarOrcDto.getCabParametro());
        exportarDto.setDetParametro(exportarOrcDto.getDetParametro());
        exportarDto.setDetalleDistritoElectoralEleccion(exportarOrcDto.getDetalleDistritoElectoralEleccion());
        exportarDto.setJuradoElectoralEspecial(exportarOrcDto.getJuradoElectoralEspecial());
        exportarDto.setVersionModelos(exportarOrcDto.getVersionModelos());
        return new ResponseEntity<>(exportarDto, HttpStatus.OK);
    }


}
