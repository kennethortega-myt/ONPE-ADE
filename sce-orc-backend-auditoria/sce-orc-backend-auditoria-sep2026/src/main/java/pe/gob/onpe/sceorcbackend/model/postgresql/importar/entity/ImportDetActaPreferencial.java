package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
@Table(name = "det_acta_preferencial")
public class ImportDetActaPreferencial {

    @Id
    @Column(name = "n_det_acta_preferencial_pk", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_det_acta", referencedColumnName = "n_det_acta_pk")
    private ImportDetActa detActa;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private ImportDistritoElectoral distritoElectoral;

    @Column(name = "n_posicion", nullable = false)
    private Integer posicion;

    @Column(name = "n_lista", nullable = false)
    private Integer lista;

    @Column(name = "n_votos")
    private Long votos;

    @Column(name = "n_estado")
    private Integer estado;

    @Column(name = "n_votos_automatico")
    private Long votosAutomatico;

    @Column(name = "n_votos_manual_1")
    private Long votosManual1;

    @Column(name = "n_votos_manual_2")
    private Long votosManual2;

    @Column(name = "c_estado_error_material", length = 10)
    private String estadoErrorMaterial;

    @Column(name = "c_ilegible", length = 10)
    private String ilegible;

    @Column(name ="c_ilegible_v1")
    private String ilegiblev1;

    @Column(name ="c_ilegible_v2")
    private String ilegiblev2;

    @Column(name = "n_activo")
	private Integer activo;
    
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
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