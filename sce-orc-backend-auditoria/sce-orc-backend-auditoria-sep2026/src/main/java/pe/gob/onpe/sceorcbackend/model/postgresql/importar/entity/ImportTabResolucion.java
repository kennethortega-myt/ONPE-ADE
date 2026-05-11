package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;



import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tab_resolucion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportTabResolucion implements Serializable {

	private static final long serialVersionUID = 2534249018492491725L;

	@Id
    @Column(name = "n_resolucion_pk")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_resolucion", referencedColumnName = "n_archivo_pk")
    private ImportArchivo archivoResolucion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_resolucion_pdf", referencedColumnName = "n_archivo_pk")
    private ImportArchivo archivoResolucionPdf;

    @Column(name = "n_procedencia")
    private Integer	procedencia;

    @Column(name = "d_fecha_resolucion")
    private Date fechaResolucion;

    @Column(name = "c_numero_expediente")
    private String numeroExpediente;

    @Column(name = "c_numero_resolucion")
    private String numeroResolucion;

    @Column(name = "n_tipo_resolucion")
    private Integer tipoResolucion;

    @Column(name = "c_estado_resolucion")
    private String estadoResolucion;

    @Column(name = "c_estado_digitalizacion")
    private String estadoDigitalizacion;

    @Column(name = "c_observacion_digitalizacion")
    private String observacionDigitalizacion;

    @Column(name = "n_numero_paginas")
    private Integer numeroPaginas;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String 	audUsuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date audFechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String	audUsuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    @Setter(AccessLevel.NONE)
    private Date audFechaModificacion;

    @Column(name = "n_asignado")
    private Integer asignado;

    @Column(name = "c_aud_usuario_asignado")
    private String audUsuarioAsignado;

    @Column(name = "d_aud_fecha_asignado")
    private Date audFechaAsignado;

    @PrePersist
	public void prePersist() {
		this.audFechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.audFechaModificacion = new Date();
	}

}
