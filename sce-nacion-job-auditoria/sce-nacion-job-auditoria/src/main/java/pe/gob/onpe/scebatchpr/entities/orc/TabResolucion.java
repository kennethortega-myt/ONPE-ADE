package pe.gob.onpe.scebatchpr.entities.orc;



import lombok.*;

import java.io.Serializable;
import java.util.Date;

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

@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tab_resolucion")
public class TabResolucion  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5529985293541183127L;

	@Id
    @Column(name = "n_resolucion_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_resolucion")
    @SequenceGenerator(name = "generator_tab_resolucion", sequenceName = "seq_tab_resolucion_pk", allocationSize = 1)
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

}
