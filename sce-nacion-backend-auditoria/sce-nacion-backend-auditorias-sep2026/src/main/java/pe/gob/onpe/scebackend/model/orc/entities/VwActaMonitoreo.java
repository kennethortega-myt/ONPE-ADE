package pe.gob.onpe.scebackend.model.orc.entities;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@ToString
@Builder
@Entity
@Immutable
@Table(name = "vw_acta_monitoreo")
public class VwActaMonitoreo {

	@Id
	@Column(name = "n_acta_pk")
	private Long id;
	
	@Column(name = "c_grupo_acta")
	private String grupoActa;
	
	@Column(name = "n_archivo_escrutinio")
	private Long idArchivoEscrutinio;
	
	@Column(name = "n_archivo_escrutinio_pdf")
	private Long idArchivoEscrutinioPdf;
	
	@Column(name = "n_archivo_escrutinio_pdf_firmado")
	private Long idArchivoEscrutinioFirmado;
	
	@Column(name = "n_archivo_sufragio_pdf_firmado")
	private Long idArchivoSufragioFirmado;
	
	@Column(name = "n_archivo_instalacion_pdf_firmado")
	private Long idArchivoInstalacionFirmado;
	
	@Column(name = "n_archivo_instalacion_sufragio_pdf")
	private Long idArchivoInstalacionSufragioPdf;
	
	@Column(name = "n_archivo_instalacion_sufragio_pdf_firmado ")
	private Long idArchivoInstalacionSufragioFirmado;
	
	@Column(name = "n_archivo_instalacion_sufragio")
	private Long idArchivoInstalacionSufragio;
	
	@Column(name = "n_proceso_electoral")
	private Long idProcesoElectoral;
	
	@Column(name = "n_eleccion")
	private Long idEleccion;
	
	@Column(name = "n_ubigeo_nivel_1")
	private Long idUbigeoNivel1;
	
	@Column(name = "n_ubigeo_nivel_2")
	private Long idUbigeoNivel2;
	
	@Column(name = "n_ubigeo_nivel_3")
	private Long idUbigeoNivel3;
	
	@Column(name = "n_local_votacion")
	private Long idLocalVotacion;
	
	@Column(name = "c_mesa")
	private String mesaCodigo;
	
	@Column(name = "c_numero_copia")
	private String 	numeroCopia;
	
	@Column(name = "c_digito_chequeo_escrutinio")
	private String 	digitoChequeoEscrutinio;
	
	@Column(name = "c_estado_acta")
	private String  estadoActa;
	
	@Column(name = "n_ver_acta")
	private Integer verActa;
	
	@Column(name = "n_cantidad_transmisiones_faltantes")
	private Integer cantidadTransmitidasFaltantes;
	
	@Column(name = "d_aud_fecha_procesamiento_cc")
	private Date	fechaProcesamiento;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date	fechaModificacion;
	
	@Column(name = "d_aud_fecha_modificacion_cc", nullable = true)
	private Date	fechaModificacionCc;
	
	@Transient
	public String getCodigoCompleto() {
	    return Stream.of(mesaCodigo, numeroCopia, digitoChequeoEscrutinio)
	            .filter(Objects::nonNull)
	            .collect(Collectors.joining());
	}
	
}
