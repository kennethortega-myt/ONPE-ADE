package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.SceConstantes;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cab_acta_celeste")
public class ActaCeleste implements Serializable {
	
	private static final long serialVersionUID = 4113195803276814951L;
	
	@Id
    @Column(name = "n_acta_celeste_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cab_acta_celeste_pk_gen")
    @SequenceGenerator(name = "seq_cab_acta_celeste_pk_gen", sequenceName = "seq_cab_acta_celeste_pk", allocationSize = 1)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = true)
    private Acta acta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoEscrutinio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_sufragio", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoSufragio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionSufragio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio_pdf", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoEscrutinioPdf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_pdf", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionPdf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_sufragio_pdf", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoSufragioPdf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio_pdf", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionSufragioPdf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_escrutinio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoEscrutinioFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_sufragio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoSufragioFirmado;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo_instalacion_sufragio_pdf_firmado", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoInstalacionSufragioFirmado;

	@Column(name = "c_numero_copia")
	private String 	numeroCopia;
	
	@Column(name = "c_digito_chequeo_escrutinio")
	private String 	digitoChequeoEscrutinio;
	
	@Column(name = "c_digito_chequeo_instalacion")
	private String digitoChequeoInstalacion;

	@Column(name = "c_digito_chequeo_sufragio")
	private String 	digitoChequeoSufragio;
	
	@Column(name = "c_estado_digitalizacion")
	private String  estadoDigitalizacion;
		
	@Column(name = "n_digitalizacion_escrutinio")
	private Long	digitalizacionEscrutinio;
	
	@Column(name = "n_digitalizacion_instalacion")
	private Long	digitalizacionInstalacion;
	
	@Column(name = "n_digitalizacion_sufragio")
	private Long	digitalizacionSufragio;
	
	@Column(name = "n_digitalizacion_instalacion_sufragio")
	private Long 	digitalizacionInstalacionSufragio;
	
	@Column(name = "c_observacion_digitalizacion_escrutinio")
	private String	observDigEscrutinio;
	
	@Column(name = "c_observacion_digitalizacion_instalacion")
	private String	observDigInstalacion;
	
	@Column(name = "c_observacion_digitalizacion_sufragio")
	private String	observDigSufragio;
	
	@Column(name = "c_observacion_digitalizacion_instalacion_sufragio")
	private String	observDigInstalacionSufragio;
	
	@Column(name = "n_activo")
	private Integer activo;

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
	
	@Column(name = "c_usuario_control")
	private String	usuarioControlCalidad;
	
	@Column(name = "d_fecha_usuario_control")
	private Date	fechaControlCalidad;
	
	@Column(name = "n_asignado")
	private Integer asignado;

	@Column(name = "c_usuario_asignado")
	private String usuarioAsignado;
	
	@Column(name = "c_id_acta_celeste_cc")
	private String idCc;
	

	@PrePersist
    public void setDefaultValues() {
        if (Objects.isNull(activo)) {
            activo = SceConstantes.ACTIVO;
        }

        if (Objects.isNull(fechaCreacion)) {
            fechaCreacion = new Date();
        }
        
    }

	public ActaCeleste(Long id) {
		super();
		this.id = id;
	}

}