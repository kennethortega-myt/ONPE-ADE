package pe.gob.onpe.scebackend.ext.pr.dto;

import lombok.*;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrEleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrParticipacionCiudadanaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrResumenExportDto;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TramaSceDto {

	private Long idTransferencia;
    private Long idActa;
    private String vista;
    private String usuario;
    
    private List<VwPrEleccionExportDto> tramaEleccion;
	private List<VwPrResumenExportDto> tramaResumen;
	private List<VwPrParticipacionCiudadanaExportDto> tramaParticipacion;
	private List<VwPrActaExportDto> tramaActa;

}
