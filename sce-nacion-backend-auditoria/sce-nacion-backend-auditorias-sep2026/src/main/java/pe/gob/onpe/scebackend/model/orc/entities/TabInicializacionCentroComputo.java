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
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tab_inicializacion_centro_computo")
public class TabInicializacionCentroComputo implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_inicializacion_centro_computo")
    @SequenceGenerator(name = "generator_tab_inicializacion_centro_computo", sequenceName = "seq_tab_inicializacion_centro_computo_pk", allocationSize = 1)
    @Column(name = "n_inicializacion_centro_computo_pk")
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_centro_computo", referencedColumnName = "n_centro_computo_pk", nullable = false)
	private CentroComputo centroComputo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_transmision_envio", referencedColumnName = "n_transmision_envio_pk", nullable = false)
	private TransmisionEnvio transmisionEnvio;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date	fechaModificacion;
	
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
