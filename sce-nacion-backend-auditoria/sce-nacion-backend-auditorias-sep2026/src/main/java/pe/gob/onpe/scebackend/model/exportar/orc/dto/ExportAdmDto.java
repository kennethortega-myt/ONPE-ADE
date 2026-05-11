package pe.gob.onpe.scebackend.model.exportar.orc.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExportAdmDto {

	private List<AdmArchivoExportDto>				admArchivo;
	private List<AdmConfigProcesoElectoralExportDto> admConfigProcesoElectoral;
	private List<AdmDetConfigDocElectoralHistExportDto> admDetConfigDocElectoralHist;
	private List<AdmDetTipoEleccionDocElectoralHistExportDto> admDetTipoEleccionDocElectoralHist;
	private List<AdmDocElectoralExportDto> admDocElectoral;
	private List<AdmSeccionExportDto> admSeccion;
	private List<AdmCatalogoExportDto> admCatalogo;
	private List<AdmDetCatalogoEstructuraExportDto> admCatalogoEstructura;
	private List<AdmDetCatalogoReferenciaExportDto> admCatalogoReferencia;
	
}
