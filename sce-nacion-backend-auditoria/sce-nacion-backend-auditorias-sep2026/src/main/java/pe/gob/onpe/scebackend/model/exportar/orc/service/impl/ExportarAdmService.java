package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmArchivoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmCatalogoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmConfigProcesoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoReferenciaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetConfigDocElectoralHistExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetTipoEleccionDocElectoralHistExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDocElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmSeccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportAdmDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IExportarAdmService;


@Service
public class ExportarAdmService implements IExportarAdmService {
	
	@Autowired
	private ConfiguracionProcesoElectoralExportService configProcesoElectoralService;
	
	@Autowired
	private SeccionExportService seccionService;
	
	@Autowired
	private DocumentoElectoralExportService documentoElectoralService;
	
	@Autowired
	private ArchivoExportService archivoService;
	
	@Autowired
	private DetalleTipoEleccionDocumentoElectoralHistorialExportService detalleTipoEleccionDocumentoElectoralHistorialService;
	
	@Autowired
	private DetalleConfiguracionDocumentoElectoralHistorialExportExportService detalleConfiguracionDocumentoElectoralHistorialService;
	
	@Autowired
	private AdmCatalogoExportService catalogoService;
	
	@Autowired
	private AdmDetCatalogoEstructuraExportService catalogoEstructuraService;
	
	@Autowired
	private AdmDetCatalogoReferenciaExportService catalogoReferenciaService;

	@Override
	@Transactional("tenantTransactionManager")
	public ExportAdmDto exportar(String proceso, String idCc) {
		List<AdmConfigProcesoElectoralExportDto> configuracionProcesoElectoralDto = this.configProcesoElectoralService.findByCc(proceso);
		List<AdmSeccionExportDto> seccionesDto = this.seccionService.findByCc(proceso);
		List<AdmDocElectoralExportDto> documentosElectoralesDto = this.documentoElectoralService.findByCc(proceso);
		List<AdmArchivoExportDto> archivosAdminDto = this.archivoService.findByCc(proceso);
		List<AdmDetTipoEleccionDocElectoralHistExportDto> detallesTipoEleccionDocElectoralHist = this.detalleTipoEleccionDocumentoElectoralHistorialService.findByCc(proceso);
		List<AdmDetConfigDocElectoralHistExportDto> detallesConfDocElectoralHistorialDto = this.detalleConfiguracionDocumentoElectoralHistorialService.findByCc(proceso);
		List<AdmCatalogoExportDto> catalogosDto = this.catalogoService.findAll();
		List<AdmDetCatalogoEstructuraExportDto> estructurasDto = this.catalogoEstructuraService.findAll();
		List<AdmDetCatalogoReferenciaExportDto> referenciasDto = this.catalogoReferenciaService.findAll();
		ExportAdmDto exportar = new ExportAdmDto();
		exportar.setAdmConfigProcesoElectoral(configuracionProcesoElectoralDto);
		exportar.setAdmSeccion(seccionesDto);
		exportar.setAdmDocElectoral(documentosElectoralesDto);
		exportar.setAdmArchivo(archivosAdminDto);
		exportar.setAdmDetTipoEleccionDocElectoralHist(detallesTipoEleccionDocElectoralHist);
		exportar.setAdmDetConfigDocElectoralHist(detallesConfDocElectoralHistorialDto);
		exportar.setAdmCatalogo(catalogosDto);
		exportar.setAdmCatalogoEstructura(estructurasDto);
		exportar.setAdmCatalogoReferencia(referenciasDto);
		return exportar;
	}
	
}
