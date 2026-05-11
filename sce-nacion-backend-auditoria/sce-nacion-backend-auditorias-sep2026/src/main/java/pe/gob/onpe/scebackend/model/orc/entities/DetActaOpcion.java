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
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta_opcion")
public class DetActaOpcion implements Serializable {

	private static final long serialVersionUID = 7290069343226796437L;

	@Id
	@Column(name = "n_det_acta_opcion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_acta_opcion")
    @SequenceGenerator(name = "generator_det_acta_opcion", sequenceName = "seq_det_acta_opcion_pk", allocationSize = 1)
	private Integer id;
	
	@Column(name = "c_id_det_acta_opcion_cc")
	private String idDetActaOpcionCc;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_det_acta", referencedColumnName = "n_det_acta_pk", nullable = false)
	private DetActa detActa;
	
	@Column(name = "n_posicion")
	private Long	posicion;
	
	@Column(name = "n_votos")
	private Long	votos;
	
	@Column(name = "n_votos_automatico")
	private Long votosAutomatico;
	
	@Column(name = "n_votos_manual_1")
	private Long votosManual1;
	
	@Column(name = "n_votos_manual_2")
	private Long votosManual2;
	
	@Column(name = "c_estado_error_material")
	private String  estadoErrorMaterial;
	
	@Column(name = "c_ilegible")
	private String 	ilegible;
	
	@Column(name = "c_ilegible_v1")
	private String 	ilegiblev1;
	
	@Column(name = "c_ilegible_v2")
	private String 	ilegiblev2;
	
	@Column(name = "c_ilegible_automatico")
	private String 	ilegibleAutomatico;
	
	@Column(name = "n_activo")
    private Integer activo;

	@Column(name = "c_aud_usuario_procesamiento_cc")
    private String usuarioProcesamiento;
	
	@Column(name = "d_aud_fecha_procesamiento_cc")
    private Date fechaProcesamiento;
	
    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;
	
}
