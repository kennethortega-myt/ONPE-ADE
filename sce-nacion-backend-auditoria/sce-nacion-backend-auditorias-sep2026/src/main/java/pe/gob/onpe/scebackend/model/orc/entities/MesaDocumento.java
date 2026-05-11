package pe.gob.onpe.scebackend.model.orc.entities;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Date;

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
public class MesaDocumento implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    @Column(name = "n_det_mesa_documento_electoral_archivo_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_mesa_documento_electoral")
    @SequenceGenerator(name = "generator_det_mesa_documento_electoral", sequenceName = "seq_det_mesa_documento_electoral_archivo_pk", allocationSize = 1)
    private Long id;
    
    @Column(name = "c_id_det_mesa_documento_electoral_archivo_cc")
    private String idCc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo", referencedColumnName = "n_archivo_pk", nullable = true)
    private Archivo archivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_documento_electoral", referencedColumnName = "n_documento_electoral_pk", nullable = true)
    private OrcDocumentoElectoral documentoElectoral;

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
    private Date fechaModificacion;
    
    
}
