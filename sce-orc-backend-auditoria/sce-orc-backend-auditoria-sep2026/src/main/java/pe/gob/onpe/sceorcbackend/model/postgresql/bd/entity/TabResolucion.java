package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tab_resolucion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TabResolucion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "n_resolucion_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_resolucion")
    @SequenceGenerator(name = "generator_tab_resolucion", sequenceName = "seq_tab_resolucion_pk", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_resolucion", referencedColumnName = "n_archivo_pk")
    private Archivo archivoResolucion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_resolucion_pdf", referencedColumnName = "n_archivo_pk")
    private Archivo archivoResolucionPdf;

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
    private Date audFechaModificacion;

    @Column(name = "n_asignado")
    private Integer asignado;

    @Column(name = "c_aud_usuario_asignado")
    private String audUsuarioAsignado;

    @Column(name = "d_aud_fecha_asignado")
    private Date audFechaAsignado;
    
    @Column(name = "c_usuario_control")
    private String  usuarioControl;

    @Column(name = "d_aud_fecha_usuario_control")
    private Date	fechaUsuarioControl;

    @Column(name = "c_usuario_asociacion")
    private String  usuarioAsociacion;

    @Column(name = "d_fecha_usuario_asociacion")
    private Date	fechaUsuarioAsociacion;

    @Column(name = "n_ambito_electoral")
    private Long idAmbitoElectoral;
    

}
