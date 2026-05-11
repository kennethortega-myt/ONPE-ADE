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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
@Table(name = "cab_acta_historial")
public class ImportActaHistorial implements Serializable  {

	private static final long serialVersionUID = -8588067310902006618L;

	@Id
	@Column(name = "n_acta_historial_pk")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = false)
	private ImportActa acta;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "actaHistorial", cascade = CascadeType.ALL)
	private Set<ImportDetActaHistorial> detalles;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private ImportMesa mesa;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_det_ubigeo_eleccion", referencedColumnName = "n_det_ubigeo_eleccion_pk", nullable = false)
	private ImportUbigeoEleccion ubigeoEleccion;
	
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
	
	@Column(name = "c_numero_copia")
	private String 	numeroCopia;
	
	@Column(name = "c_numero_lote")
	private String 	numeroLote;
	
	@Column(name = "c_tipo_lote")
	private String 	tipoLote;
	
	@Column(name = "n_electores_habiles")
	private Long   	electoresHabiles;
	
	@Column(name = "n_cvas")
	private Long   	cvas;
	
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
	
	@Column(name = "n_digitacion_firmas_manual")
	private Long	digitacionFirmasManual;
	
	@Column(name = "n_digitacion_firmas_automatico")
	private Long	digitacionFirmasAutomatico;
	
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
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "n_tipo_transmision")
	private Integer tipoTransmision;
	
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
