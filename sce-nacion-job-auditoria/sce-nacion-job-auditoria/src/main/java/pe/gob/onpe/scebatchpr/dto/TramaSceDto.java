package pe.gob.onpe.scebatchpr.dto;

import lombok.*;

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
	private List<VwPrEleccionExportDto> tramaDiputados;
	private List<VwPrEleccionExportDto> tramaParlamento;
	private List<VwPrEleccionExportDto> tramaPresidenciales;
	private List<VwPrEleccionExportDto> tramaSenadoresDistritoElectoralMultiple;
	private List<VwPrEleccionExportDto> tramaSenadoresDistritoNacionalUnico;
	private List<VwPrMesaExportDto> tramaMesa;
	private List<VwPrEleccionExportDto> tramaRevocatoriaDistrital;
	

}
