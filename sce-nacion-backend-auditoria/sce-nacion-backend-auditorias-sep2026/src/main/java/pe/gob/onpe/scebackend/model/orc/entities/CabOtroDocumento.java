package pe.gob.onpe.scebackend.model.orc.entities;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "cab_otro_documento")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabOtroDocumento implements Serializable  {

	private static final long serialVersionUID = -3181958953029708449L;

	@Id
    @Column(name = "n_otro_documento_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_cab_otro_documento")
    @SequenceGenerator(name = "generator_cab_otro_documento", sequenceName = "seq_cab_otro_documento_pk", allocationSize = 1)
    private Long id;
	
	@Column(name = "c_id_cab_otro_documento_cc")
	private String idCc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_centro_computo", referencedColumnName = "n_centro_computo_pk")
    private CentroComputo centroComputo;

    @Column(name = "c_numero_documento")
    private String numeroDocumento;

    @Column(name = "c_tipo_documento")
    private String codTipoDocumento;

    @Column(name = "n_numero_paginas")
    private Integer numeroPaginas;

    @Column(name = "c_estado_digitalizacion")
    private String estadoDigitalizacion;

    @Column(name = "c_estado_documento")
    private String estadoDocumento;

    @Column(name = "n_archivo_otro_documento")
    private Long idArchivoOtroDocumento;

    @Column(name = "n_archivo_otro_documento_pdf")
    private Long idArchivoOtroDocumentoPdf;

    @Column(name = "c_usuario_control")
    private String usuarioControl;

    @Column(name = "d_aud_fecha_usuario_control")
    private Date fechaUsuarioControl;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String audUsuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date audFechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String audUsuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date audFechaModificacion;
	
}
