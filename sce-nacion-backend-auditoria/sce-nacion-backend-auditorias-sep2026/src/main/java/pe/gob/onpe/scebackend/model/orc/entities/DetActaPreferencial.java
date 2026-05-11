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
import lombok.ToString;
import pe.gob.onpe.scebackend.model.entities.DatosAuditoria;
import pe.gob.onpe.scebackend.utils.SceConstantes;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta_preferencial")
public class DetActaPreferencial extends DatosAuditoria implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "det_acta_preferencial_seq")
    @SequenceGenerator(name = "det_acta_preferencial_seq", sequenceName = "seq_det_acta_preferencial_pk", allocationSize = 1)
    @Column(name = "n_det_acta_preferencial_pk", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_det_acta", referencedColumnName = "n_det_acta_pk")
    private DetActa detActa;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private DistritoElectoral distritoElectoral;

    @Column(name = "n_posicion", nullable = false)
    private Integer posicion;

    @Column(name = "n_lista", nullable = false)
    private Integer lista;

    @Column(name = "n_votos")
    private Long votos;

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
    
    @Column(name = "c_ilegible_v1")
	private String 	ilegiblev1;
	
	@Column(name = "c_ilegible_v2")
	private String 	ilegiblev2;

    @Column(name = "n_activo")
	private Integer activo;
    
    @Column(name = "c_id_det_acta_preferencial_cc")
	private String idOrc;
	
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