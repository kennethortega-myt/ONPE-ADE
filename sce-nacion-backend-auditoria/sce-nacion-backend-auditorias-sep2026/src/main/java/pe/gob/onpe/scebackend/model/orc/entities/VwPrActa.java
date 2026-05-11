package pe.gob.onpe.scebackend.model.orc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.scebackend.model.deserializer.JsonStringDeserializer;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "vw_pr_acta")
public class VwPrActa implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_acta_pk")
	@JsonProperty("n_acta_pk")
	private Long idActa;
	
	@Column(name = "n_mesa")
	@JsonProperty("n_mesa")
	private Long idMesa;
	
	@Column(name = "n_solucion_tecnologica")
	@JsonProperty("n_solucion_tecnologica")
	private Long  idSolucionTecnologica;
	
	@Column(name = "c_descripcion_solucion_tecnologica")
	@JsonProperty("c_descripcion_solucion_tecnologica")
	private String  descripcionSolucionTecnologica;
	
	@Column(name = "c_mesa")
	@JsonProperty("c_mesa")
	private String codigoMesa;
	
	@Column(name = "c_numero_copia")
	@JsonProperty("c_numero_copia")
	private String numeroCopia;
	
	@Column(name = "c_digito_chequeo_escrutinio")
	@JsonProperty("c_digito_chequeo_escrutinio")
	private String digitoChequeoEscrutinio;
	
	@Column(name = "c_digito_chequeo_instalacion")
	@JsonProperty("c_digito_chequeo_instalacion")
	private String digitoChequeoInstalacion;
	
	@Column(name = "c_digito_chequeo_sufragio")
	@JsonProperty("c_digito_chequeo_sufragio")
	private String digitoChequeoSufragio;
	
	@Column(name = "n_det_ubigeo_eleccion")
	@JsonProperty("n_det_ubigeo_eleccion")
	private Long idUbigeoEleccion;
	
	@Column(name = "n_eleccion")
	@JsonProperty("n_eleccion")
	private Long idEleccion;
	
	@Column(name = "n_ambito_geografico")
	@JsonProperty("n_ambito_geografico")
	private Integer idAmbitoGeografico;
	
	@Column(name = "n_ubigeo_nivel_01")
	@JsonProperty("n_ubigeo_nivel_01")
	private Long ubigeoNivel01;
	
	@Column(name = "n_ubigeo_nivel_02")
	@JsonProperty("n_ubigeo_nivel_02")
	private Long ubigeoNivel02;
	
	@Column(name = "n_ubigeo")
	@JsonProperty("n_ubigeo")
	private Long idUbigeo;
	
	@Column(name = "c_nombre_ubigeo_nivel_01")
	@JsonProperty("c_nombre_ubigeo_nivel_01")
	private String ubigeoNombreNivel01;
	
	@Column(name = "c_nombre_ubigeo_nivel_02")
	@JsonProperty("c_nombre_ubigeo_nivel_02")
	private String ubigeoNombreNivel02;
	
	@Column(name = "c_nombre_ubigeo_nivel_03")
	@JsonProperty("c_nombre_ubigeo_nivel_03")
	private String ubigeoNombreNivel03;
	
	@Column(name = "c_centro_poblado")
	@JsonProperty("c_centro_poblado")
	private String centroPoblado;
	
	@Column(name = "n_local_votacion")
	@JsonProperty("n_local_votacion")
	private Long idLocalVotacion;
	
	@Column(name = "c_nombre_local_votacion")
	@JsonProperty("c_nombre_local_votacion")
	private String nombreLocalVotacion;
	
	@Column(name = "c_codigo_local_votacion")
	@JsonProperty("c_codigo_local_votacion")
	private String codigoLocalVotacion;
	
	@Column(name = "n_total_electores_habiles")
	@JsonProperty("n_total_electores_habiles")
	private Integer totalElectoresHabiles;
	
	@Column(name = "n_total_votos_emitidos")
	@JsonProperty("n_total_votos_emitidos")
	private Integer totalVotosEmitidos;
	
	@Column(name = "n_total_votos_validos")
	@JsonProperty("n_total_votos_validos")
	private Integer totalVotosValidos;
	
	@Column(name = "n_total_asistentes")
	@JsonProperty("n_total_asistentes")
	private Integer totalAsistentes;
	
	@Column(name = "n_porcentaje_participacion_ciudadana")
	@JsonProperty("n_porcentaje_participacion_ciudadana")
	private Double  porcentajeParticipacionCiudadana;
	
	@Column(name = "c_estado_acta")
	@JsonProperty("c_estado_acta")
	private String  estadoActa;
	
	@Column(name = "c_estado_computo")
	@JsonProperty("c_estado_computo")
	private String  estadoComputo;
	
	@Column(name = "c_codigo_estado_acta")
	@JsonProperty("c_codigo_estado_acta")
	private String  codigoEstadoActa;
	
	@Column(name = "c_descripcion_estado_acta")
	@JsonProperty("c_descripcion_estado_acta")
	private String  descripcionEstadoActa;
	
	@Column(name = "c_descripcion_sub_estado_acta")
	@JsonProperty("c_descripcion_sub_estado_acta")
	private String  descripcionSubEstadoActa;
	
	@Column(name = "n_distrito_electoral")
	@JsonProperty("n_distrito_electoral")
	private Long  idDistritoElectoral;
	
	@Column(name = "c_estado_acta_resolucion")
	@JsonProperty("c_estado_acta_resolucion")
	private String  estadoActaResolucion;
	
	@Column(name = "c_descripcion_estado_acta_resolucion")
	@JsonProperty("c_descripcion_estado_acta_resolucion")
	private String  estadoDescripcionActaResolucion;
	
	@Column(name = "c_detalle")
	@JsonProperty("c_detalle")
	@JsonDeserialize(using = JsonStringDeserializer.class)
	private String  detalle;
	
	@Column(name = "c_linea_tiempo")
	@JsonProperty("c_linea_tiempo")
	@JsonDeserialize(using = JsonStringDeserializer.class)
	private String  lineaTiempo;
	
}
