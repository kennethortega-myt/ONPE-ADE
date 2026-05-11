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
import pe.gob.onpe.scebackend.model.entities.DatosAuditoria;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta_preferencial_historial")
public class DetActaPreferencialHistorial extends DatosAuditoria implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_acta_preferencial_historial")
    @SequenceGenerator(name = "generator_det_acta_preferencial_historial", sequenceName = "seq_det_acta_preferencial_historial_pk", allocationSize = 1)
    @Column(name = "n_det_acta_preferencial_historial_pk", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_det_acta_historial", referencedColumnName = "n_det_acta_historial_pk")
    private DetActaHistorial detActaHistorial;
    
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

    @Column(name = "n_activo")
	private Integer activo;
}
