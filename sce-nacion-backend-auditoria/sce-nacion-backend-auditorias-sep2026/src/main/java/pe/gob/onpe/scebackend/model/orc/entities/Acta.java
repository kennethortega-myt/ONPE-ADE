package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.scebackend.utils.SceConstantes;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "cab_acta")
public class Acta implements Serializable {
	
	private static final long serialVersionUID = 4113195803276814951L;

	@Id
	@Column(name = "n_acta_pk")
	private Long    id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "acta", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude private Set<DetActa> detalles;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoEscrutinio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionSufragio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio_pdf", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoEscrutinioPdf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio_pdf", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionSufragioPdf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoEscrutinioFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionSufragioFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_sufragio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoSufragioFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_acta", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoActa;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private Mesa mesa;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_det_ubigeo_eleccion", referencedColumnName = "n_det_ubigeo_eleccion_pk", nullable = false)
	@EqualsAndHashCode.Exclude private UbigeoEleccion ubigeoEleccion;

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
	private Long   	cvasAutomatico;
	
	@Column(name = "n_cvas_v1")
	private Long   	cvasv1;
	
	@Column(name = "n_cvas_v2")
	private Long   	cvasv2;
	
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
	
	@Column(name = "c_verificador")
	private String  verificador;
	
	@Column(name = "c_verificador_v2")
	private String  verificador2;
	
	@Column(name = "c_ilegible_cvas")
	private String 	ilegibleCvas;
	
	@Column(name = "c_ilegible_cvas_v1")
	private String 	ilegibleCvasv1;
	
	@Column(name = "c_ilegible_cvas_v2")
	private String 	ilegibleCvasv2;
	
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
	private Date	fechaModificacion;
	
	@Column(name = "d_aud_fecha_modificacion_cc")
	private Date	fechaModificacionCc;
	
	@Column(name = "c_aud_usuario_procesamiento_cc")
	private String	usuarioProcesamiento;
	
	@Column(name = "d_aud_fecha_procesamiento_cc")
	private Date	fechaProcesamiento;
	
	@Column(name = "n_transmision")
	private Long idTransmision;
	
	@Column(name = "c_codigo_centro_computo")
	private String centroCc;
	
	@PrePersist
    public void setDefaultValues() {
        if (Objects.isNull(activo)) {
            activo = SceConstantes.ACTIVO;
        }

        if (Objects.isNull(fechaCreacion)) {
            fechaCreacion = new Date();
        }
    }
	

}
