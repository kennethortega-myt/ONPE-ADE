package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cab_acta")
public class ImportActa implements Serializable {
	
	private static final long serialVersionUID = 4113195803276814951L;

	@Id
	@Column(name = "n_acta_pk")
	private Long    id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "acta", cascade = CascadeType.ALL)
	private Set<ImportDetActa> detalles;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "acta", cascade = CascadeType.ALL)
	private Set<ImportDetActaResolucion> detResoluciones;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio", referencedColumnName = "n_archivo_pk", nullable = true)
	private ImportArchivo archivoEscrutinio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio", referencedColumnName = "n_archivo_pk", nullable = true)
	private ImportArchivo archivoInstalacionSufragio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private ImportArchivo archivoEscrutinioFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private ImportArchivo archivoInstalacionSufragioFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private ImportMesa mesa;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_det_ubigeo_eleccion", referencedColumnName = "n_det_ubigeo_eleccion_pk", nullable = false)
	private ImportUbigeoEleccion ubigeoEleccion;

	@Column(name = "c_numero_copia")
	private String 	numeroCopia;
	
	@Column(name = "c_numero_lote")
	private String 	numeroLote;
	
	@Column(name = "c_digito_chequeo_escrutinio")
	private String 	digitoChequeoEscrutinio;

	@Column(name = "c_digito_chequeo_instalacion")
	private String digitoChequeoInstalacion;

	@Column(name = "c_digito_chequeo_sufragio")
	private String 	digitoChequeoSufragio;
	
	@Column(name = "c_tipo_lote")
	private String 	tipoLote;
	
	@Column(name = "n_electores_habiles")
	private Long   	electoresHabiles;
	
	@Column(name = "n_cvas")
	private Long   	cvas;

	@Column(name = "n_cvas_automatico")
	private Long cvasAutomatico;

	@Column(name = "n_cvas_v1")
	private Long cvasV1;

	@Column(name = "n_cvas_v2")
	private Long cvasV2;

	@Column(name ="c_ilegible_cvas")
	private String ilegibleCvas;

	@Column(name ="c_ilegible_cvas_v1")
	private String ilegibleCvasV1;

	@Column(name ="c_ilegible_cvas_v2")
	private String ilegibleCvasV2;
	
	@Column(name = "n_votos_calculados")
	private Long	votosCalculados;
	
	@Column(name = "n_total_votos")
	private Long	totalVotos;
	
	@Column(name = "c_estado_acta")
	private String  estadoActa;
	
	@Column(name = "c_estado_computo")
	private String  estadoCc;
	
	@Column(name = "c_estado_acta_resolucion")
	private String  estadoActaResolucion;
	
	@Column(name = "c_estado_digitalizacion")
	private String  estadoDigitalizacion;
	
	@Column(name = "c_estado_error_material")
	private String  estadoErrorMaterial;
	
	@Column(name = "n_digitalizacion_escrutinio")
	private Long	digitalizacionEscrutinio;
	
	@Column(name = "n_digitalizacion_instalacion_sufragio")
	private Long 	digitalizacionInstalacionSufragio;
	
	@Column(name = "n_control_digitalizacion_escrutinio")
	private Long	controlDigEscrutinio;
	
	@Column(name = "n_control_digitalizacion_instalacion_sufragio")
	private Long	controlDigInstalacionSufragio;
	
	@Column(name = "c_observacion_digitalizacion_escrutinio")
	private String	observDigEscrutinio;
	
	@Column(name = "c_observacion_digitalizacion_instalacion_sufragio")
	private String	observDigInstalacionSufragio;
	
	@Column(name = "n_digitacion_horas")
	private Long    digitacionHoras;
	
	@Column(name = "n_digitacion_votos")
	private Long	digitacionVotos;
	
	@Column(name = "n_digitacion_observaciones")
	private Long	digitacionObserv;
	
	@Column(name = "n_digitacion_firmas_automatico")
	private Long	digitacionFirmasAutomatico;
	
	@Column(name = "n_digitacion_firmas_manual")
	private Long	digitacionFirmasManual;

	@Column(name ="n_digitacion_firmas_manual_v1")
	private Long digitacionFirmasManualV1;

	@Column(name ="n_digitacion_firmas_manual_v2")
	private Long digitacionFirmasManualV2;

	@Column(name ="n_digitacion_sin_datos_manual")
	private Long digitacionSinDatosManual;

	@Column(name ="n_digitacion_sin_datos_manual_v1")
	private Long digitacionSinDatosManualV1;

	@Column(name ="n_digitacion_sin_datos_manual_v2")
	private Long digitacionSinDatosManualV2;

	@Column(name ="n_digitacion_solicitud_nulidad_manual")
	private Long digitacionSolicitudNulidadManual;

	@Column(name ="n_digitacion_solicitud_nulidad_v1")
	private Long digitacionSolicitudNulidadManualV1;

	@Column(name ="n_digitacion_solicitud_nulidad_v2")
	private Long digitacionSolicitudNulidadManualV2;

	@Column(name = "n_control_digitacion")
	private Long	controlDigitacion;
	
	@Column(name = "c_hora_escrutinio_automatico")
	private String  horaEscrutinioAutomatico;
	
	@Column(name = "c_hora_escrutinio_manual")
	private String 	horaEscrutinioManual;
	
	@Column(name = "c_hora_instalacion_automatico")
	private String	horaInstalacionAutomatico;
	
	@Column(name = "c_hora_instalacion_manual")
	private String  horaInstalacionManual;
	
	@Column(name = "c_descripcion_observacion_automatico")
	private String 	descripcionObservAutomatico;
	
	@Column(name = "c_descripcion_observacion_manual")
	private String	descripcionObservManual;
	
	@Column(name = "n_escrutinio_firma_mm1_automatico")
	private Long	escrutinioFirmaMm1Automatico;
	
	@Column(name = "n_escrutinio_firma_mm2_automatico")
	private Long	escrutinioFirmaMm2Automatico;
	
	@Column(name = "n_escrutinio_firma_mm3_automatico")
	private Long	escrutinioFirmaMm3Automatico;
	
	@Column(name = "n_instalacion_firma_mm1_automatico")
	private Long	instalacionFirmaMm1Automatico;
	
	@Column(name = "n_instalacion_firma_mm2_automatico")
	private Long	instalacionFirmaMm2Automatico;
	
	@Column(name = "n_instalacion_firma_mm3_automatico")
	private Long	instalacionFirmaMm3Automatico;
	
	@Column(name = "n_sufragio_firma_mm1_automatico")
	private Long	sufragioFirmaMm1Automatico;
	
	@Column(name = "n_sufragio_firma_mm2_automatico")
	private Long	sufragioFirmaMm2Automatico;

	@Column(name = "n_sufragio_firma_mm3_automatico")
	private Long	sufragioFirmaMm3Automatico;

	@Column(name = "n_asignado")
	private Integer asignado;

	@Column(name = "c_usuario_asignado")
	private String usuarioAsignado;

	@Column(name = "c_verificador")
	private String verificador;

	@Column(name = "c_verificador_v2")
	private String verificador2;

	@Column(name ="n_autorizacion_id")
	private Long autorizacionId;

	@Column(name ="c_reprocesar")
	private String reprocesar;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "n_tipo_transmision")
	private Integer tipoTransmision;
	
	@Column(name = "n_solucion_tecnologica")
	private Integer solucionTecnologica;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	@Setter(AccessLevel.NONE)
	private Date	fechaModificacion;
	
	@Column(name = "c_aud_usuario_procesamiento_cc")
	private String	usuarioProcesamiento;
	
	@Column(name = "d_aud_fecha_procesamiento_cc")
	private Date	fechaProcesamiento;

	@PrePersist
    public void prePersist() {
    	this.fechaCreacion = new Date();
    }
    
    
    @PreUpdate
    public void preUpdate() {
    	this.fechaModificacion = new Date();
    }

}
