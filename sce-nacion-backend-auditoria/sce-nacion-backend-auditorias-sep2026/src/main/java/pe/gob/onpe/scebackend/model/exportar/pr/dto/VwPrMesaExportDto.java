package pe.gob.onpe.scebackend.model.exportar.pr.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VwPrMesaExportDto {


	private Long id;
	
	private String tipoFiltro;
	
	private Long idDistritoElectoral;
	
	private Long ambitoGeografico;
	
	private Long ubigeoNivel01;
	
	private Long ubigeoNivel02;
	
	private Long ubigeoNivel03;
	
	private Long totalMesas;
	
	private Long mesasInstaladas;
	
	private Long mesasNoInstaladas;
	
	private Long mesasPorInformar;
	

}
