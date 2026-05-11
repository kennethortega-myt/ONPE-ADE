package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "mae_opcion_voto")
public class OpcionVoto implements Serializable {

	private static final long serialVersionUID = -3747836975062594228L;
	
	@Id
	@Column(name = "n_opcion_voto_pk")
	private Integer id;
	
	@Column(name = "c_codigo")
	private String codigo;
	
	@Column(name = "c_descripcion")
	private String descripcion;
	
	@Column(name = "n_posicion")
	private Integer posicion;
	
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
