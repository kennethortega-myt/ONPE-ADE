package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;


import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.Date;

import lombok.AccessLevel;
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
@Table(name = "det_mesa_documento_electoral_archivo")
public class ImportMesaDocumento {

    @Id
    @Column(name = "n_det_mesa_documento_electoral_archivo_pk")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
    private ImportMesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo", referencedColumnName = "n_archivo_pk", nullable = true)
    private ImportArchivo archivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_documento_electoral", referencedColumnName = "n_documento_electoral_pk", nullable = true)
    private DocumentoElectoral admDocumentoElectoral;

    @Column(name = "c_tipo_archivo")
    private String tipoArchivo;

    @Column(name = "n_pagina")
    private Integer pagina;

    @Column(name = "c_descripcion_observacion")
    private String descripcionObservacion;

    @Column(name = "n_digitalizacion")
    private Integer digitalizacion;

    @Column(name = "c_estado_digitalizacion")
    private String estadoDigitalizacion;

    @Column(name = "c_observacion_digitalizacion")
    private String observacionDigitalizacion;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    @Setter(AccessLevel.NONE)
    private Date fechaModificacion;
    
    @PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
}
