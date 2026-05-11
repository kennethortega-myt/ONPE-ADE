package pe.gob.onpe.scebatchpr.entities.orc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.scebatchpr.deserializer.JsonStringDeserializer;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class VwPrEleccion {

	
	@Id
	@Column(name = "n_id_fila")
	@JsonProperty("n_id_fila")
	private Integer idFila;
	
	@Column(name = "n_tipo_eleccion")
	@JsonProperty("n_tipo_eleccion")
	private Integer tipoEleccion;
	
	@Column(name = "n_det_ubigeo_eleccion")
	@JsonProperty("n_det_ubigeo_eleccion")
	private Long idDetUbigeoEleccion;
	
	@Column(name = "c_tipo_filtro")
	@JsonProperty("c_tipo_filtro")
	private String  tipoFiltro;
	
	@Column(name = "n_ambito_geografico")
	@JsonProperty("n_ambito_geografico")
	private Integer ambitoGeografico;
	
	@Column(name = "n_ubigeo_nivel_01")
	@JsonProperty("n_ubigeo_nivel_01")
	private Integer ubigeoNivel01;
	
	@Column(name = "n_ubigeo_nivel_02")
	@JsonProperty("n_ubigeo_nivel_02")
	private Integer ubigeoNivel02;
	
	@Column(name = "n_ubigeo_nivel_03")
	@JsonProperty("n_ubigeo_nivel_03")
	private Integer ubigeoNivel03;
	
	@Column(name = "n_total_electores_habiles")
	@JsonProperty("n_total_electores_habiles")
	private Integer totalElectoresHabiles;
	
	@Column(name = "n_participacion_ciudadana")
	@JsonProperty("n_participacion_ciudadana")
	private Integer participacionCiudadana;
	
	@Column(name = "n_porcentaje_participacion_ciudadana")
	@JsonProperty("n_porcentaje_participacion_ciudadana")
	private Double  porcentParticipacionCiudadana;
	
	@Column(name = "n_total_actas")
	@JsonProperty("n_total_actas")
	private Integer totalActas;
	
	@Column(name = "n_actas_contabilizadas")
	@JsonProperty("n_actas_contabilizadas")
	private Integer actasContabilizadas;
	
	@Column(name = "n_porcentaje_actas_contabilizadas")
	@JsonProperty("n_porcentaje_actas_contabilizadas")
	private Double  porcentajeActasContabilizadas;

	@Column(name = "n_actas_observadas_enviadas")
	@JsonProperty("n_actas_observadas_enviadas")
	private Integer actasObservadasEnviadas;
	
	@Column(name = "n_porcentaje_actas_observadas_enviadas")
	@JsonProperty("n_porcentaje_actas_observadas_enviadas")
	private Double  porcentajeActasObservadasEnviadas;
	
	@Column(name = "n_actas_pendientes")
	@JsonProperty("n_actas_pendientes")
	private Integer actasPendientes;
	
	@Column(name = "n_porcentaje_actas_pendientes")
	@JsonProperty("n_porcentaje_actas_pendientes")
	private Double  porcentajeActasPendientes;
	
	@Column(name = "n_total_votos_emitidos")
	@JsonProperty("n_total_votos_emitidos")
	private Integer totalVotosEmitidos;
	
	@Column(name = "n_total_votos_validos")
	@JsonProperty("n_total_votos_validos")
	private Integer totalVotosValidos;
	
	@Column(name = "c_detalle")
	@JsonProperty("c_detalle")
	@JsonDeserialize(using = JsonStringDeserializer.class)
	private String  detalle;

}
