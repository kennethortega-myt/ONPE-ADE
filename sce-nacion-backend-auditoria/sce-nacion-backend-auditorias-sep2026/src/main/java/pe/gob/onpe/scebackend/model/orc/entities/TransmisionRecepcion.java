package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_transmision_recepcion")
public class TransmisionRecepcion implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_transmision_recepcion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_transmision_recepcion")
    @SequenceGenerator(name = "generator_transmision_recepcion", sequenceName = "seq_tab_transmision_recepcion_pk", allocationSize = 1)
	private Long	id;

	@Column(name="n_acta")
	private Long  acta;

	@Column(name = "c_trama_dato")
	private String	tramaDato;
	
	@Column(name = "c_trama_imagen")
	private String	tramaImagen;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_procesamiento_cc")
	private String	usuarioProcesamiento;
	
	@Column(name = "d_aud_fecha_procesamiento_cc")
	private Date	fechaProcesamiento;
	
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
