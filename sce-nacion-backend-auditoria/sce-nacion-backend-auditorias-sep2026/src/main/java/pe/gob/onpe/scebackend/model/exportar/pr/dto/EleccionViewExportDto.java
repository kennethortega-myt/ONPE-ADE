package pe.gob.onpe.scebackend.model.exportar.pr.dto;

import java.util.List;
import java.util.Map;

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
public class EleccionViewExportDto {

	private List<EleccionExportDto> elecciones;
	private Map<String, List<VwPrEleccionExportDto>> vistas;
	
}
