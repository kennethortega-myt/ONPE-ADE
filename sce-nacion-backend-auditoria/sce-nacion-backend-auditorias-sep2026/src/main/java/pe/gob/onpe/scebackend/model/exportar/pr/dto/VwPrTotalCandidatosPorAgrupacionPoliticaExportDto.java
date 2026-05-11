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
public class VwPrTotalCandidatosPorAgrupacionPoliticaExportDto {

	private Integer idFila;
	private Integer ambitoGeografico;
	private Integer eleccion;
    private Integer distritoElectoral;
    private Integer ubigeoNivel01;
    private Integer ubigeoNivel02;
    private Integer ubigeo;
    private Integer detUbigeoEleccion;
    private Integer agrupacionPolitica;
    private Integer posicion;
    private String codigo;
    private String descripcion;
    private String totalCandidatos;
}
