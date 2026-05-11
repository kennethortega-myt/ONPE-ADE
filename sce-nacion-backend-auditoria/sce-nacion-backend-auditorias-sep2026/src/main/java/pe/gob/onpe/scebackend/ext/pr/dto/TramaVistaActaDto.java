package pe.gob.onpe.scebackend.ext.pr.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TramaVistaActaDto {

	private Integer idFila;
	private Integer numeroMesa;
	private String mesa;
	private String numeroCopia;
	private Integer detalleUbigeoEleccion;
	private Integer idEleccion;
	private Integer ambitoGeografico;
	private String ubigeoNivel01;
	private String ubigeoNivel02;
	private String ubigeoNivel03;
	private String centroPoblado;
	private String codigoLocalVotacion;
	private String nombreLocalVotacion;
	private Long totalElectoresHabiles;
	private Long totalVotosEmitidos;
	private Long totalVotosValidos;
	private Long totalAsistentes;
	private Double porcentParticipacionCiudadana;
	private String estadoActa;
	private String estadoComputo;
	private String descripcionEstadoActa;

	private List<String> detalle; // cambiar
	private List<String> lineaTiempo; // cambiar

	@JsonProperty("idFila")
	public Integer getIdFila() {
		return idFila;
	}

	@JsonProperty("n_acta_pk")
	public void setIdFila(Integer idFila) {
		this.idFila = idFila;
	}

	@JsonProperty("numeroMesa")
	public Integer getNumeroMesa() {
		return numeroMesa;
	}

	@JsonProperty("n_mesa")
	public void setNumeroMesa(Integer numeroMesa) {
		this.numeroMesa = numeroMesa;
	}

	@JsonProperty("mesa")
	public String getMesa() {
		return mesa;
	}

	@JsonProperty("c_mesa")
	public void setMesa(String mesa) {
		this.mesa = mesa;
	}

	@JsonProperty("numeroCopia")
	public String getNumeroCopia() {
		return numeroCopia;
	}

	@JsonProperty("c_numero_copia")
	public void setNumeroCopia(String numeroCopia) {
		this.numeroCopia = numeroCopia;
	}

	@JsonProperty("detalleUbigeoEleccion")
	public Integer getDetalleUbigeoEleccion() {
		return detalleUbigeoEleccion;
	}

	@JsonProperty("n_det_ubigeo_eleccion")
	public void setDetalleUbigeoEleccion(Integer detalleUbigeoEleccion) {
		this.detalleUbigeoEleccion = detalleUbigeoEleccion;
	}

	@JsonProperty("idEleccion")
	public Integer getIdEleccion() {
		return idEleccion;
	}

	@JsonProperty("n_eleccion")
	public void setIdEleccion(Integer idEleccion) {
		this.idEleccion = idEleccion;
	}

	@JsonProperty("ambitoGeografico")
	public Integer getAmbitoGeografico() {
		return ambitoGeografico;
	}

	@JsonProperty("n_ambito_geografico")
	public void setAmbitoGeografico(Integer ambitoGeografico) {
		this.ambitoGeografico = ambitoGeografico;
	}

	@JsonProperty("ubigeoNivel01")
	public String getUbigeoNivel01() {
		return ubigeoNivel01;
	}

	@JsonProperty("c_nombre_ubigeo_nivel_01")
	public void setUbigeoNivel01(String ubigeoNivel01) {
		this.ubigeoNivel01 = ubigeoNivel01;
	}

	public String getUbigeoNivel02() {
		return ubigeoNivel02;
	}

	public void setUbigeoNivel02(String ubigeoNivel02) {
		this.ubigeoNivel02 = ubigeoNivel02;
	}

	public String getUbigeoNivel03() {
		return ubigeoNivel03;
	}

	public void setUbigeoNivel03(String ubigeoNivel03) {
		this.ubigeoNivel03 = ubigeoNivel03;
	}

	public String getCentroPoblado() {
		return centroPoblado;
	}

	public void setCentroPoblado(String centroPoblado) {
		this.centroPoblado = centroPoblado;
	}

	public String getCodigoLocalVotacion() {
		return codigoLocalVotacion;
	}

	public void setCodigoLocalVotacion(String codigoLocalVotacion) {
		this.codigoLocalVotacion = codigoLocalVotacion;
	}

	public String getNombreLocalVotacion() {
		return nombreLocalVotacion;
	}

	public void setNombreLocalVotacion(String nombreLocalVotacion) {
		this.nombreLocalVotacion = nombreLocalVotacion;
	}

	public Long getTotalElectoresHabiles() {
		return totalElectoresHabiles;
	}

	public void setTotalElectoresHabiles(Long totalElectoresHabiles) {
		this.totalElectoresHabiles = totalElectoresHabiles;
	}

	public Long getTotalVotosEmitidos() {
		return totalVotosEmitidos;
	}

	public void setTotalVotosEmitidos(Long totalVotosEmitidos) {
		this.totalVotosEmitidos = totalVotosEmitidos;
	}

	public Long getTotalVotosValidos() {
		return totalVotosValidos;
	}

	public void setTotalVotosValidos(Long totalVotosValidos) {
		this.totalVotosValidos = totalVotosValidos;
	}

	public Long getTotalAsistentes() {
		return totalAsistentes;
	}

	public void setTotalAsistentes(Long totalAsistentes) {
		this.totalAsistentes = totalAsistentes;
	}

	public Double getPorcentParticipacionCiudadana() {
		return porcentParticipacionCiudadana;
	}

	public void setPorcentParticipacionCiudadana(Double porcentParticipacionCiudadana) {
		this.porcentParticipacionCiudadana = porcentParticipacionCiudadana;
	}

	public String getEstadoActa() {
		return estadoActa;
	}

	public void setEstadoActa(String estadoActa) {
		this.estadoActa = estadoActa;
	}

	public String getEstadoComputo() {
		return estadoComputo;
	}

	public void setEstadoComputo(String estadoComputo) {
		this.estadoComputo = estadoComputo;
	}

	public String getDescripcionEstadoActa() {
		return descripcionEstadoActa;
	}

	public void setDescripcionEstadoActa(String descripcionEstadoActa) {
		this.descripcionEstadoActa = descripcionEstadoActa;
	}

	public List<String> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<String> detalle) {
		this.detalle = detalle;
	}

	public List<String> getLineaTiempo() {
		return lineaTiempo;
	}

	public void setLineaTiempo(List<String> lineaTiempo) {
		this.lineaTiempo = lineaTiempo;
	}

}
