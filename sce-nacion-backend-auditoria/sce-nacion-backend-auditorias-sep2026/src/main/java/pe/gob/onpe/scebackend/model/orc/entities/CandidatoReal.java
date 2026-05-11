package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "mae_candidato_real")
public class CandidatoReal implements Serializable {

	private static final long serialVersionUID = 1582364559314052628L;

	@Id
	@Column(name = "n_candidato_real_pk")
	private Integer id;
	
	@Column(name = "n_eleccion")
	private Integer idEleccion;
	
	@Column(name = "n_distrito_electoral")
	private Integer idDistritoElectoral;
	
	@Column(name = "n_ubigeo")
	private Integer idUbigeo;
	
	@Column(name = "n_agrupacion_politica")
	private Integer idAgrupacionPolitica;
	
	@Column(name = "n_cargo")
	private Integer cargo;
	
	@Column(name = "c_documento_identidad")
	private String  documentoIdentidad;
	
	@Column(name = "c_apellido_paterno")
	private String  apellidoPaterno;
	
	@Column(name = "c_apellido_materno")
	private String  apellidoMaterno;
	
	@Column(name = "c_nombres")
	private String  nombres;
	
	@Column(name = "n_sexo")
	private Integer sexo;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "n_lista")
	private Integer lista;
	
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
