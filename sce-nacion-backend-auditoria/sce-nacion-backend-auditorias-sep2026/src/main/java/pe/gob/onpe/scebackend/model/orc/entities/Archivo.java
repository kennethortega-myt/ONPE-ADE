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

import lombok.*;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_archivo")
public class Archivo implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_archivo_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_acta_archivo")
    @SequenceGenerator(name = "generator_acta_archivo", sequenceName = "seq_tab_archivo_pk", allocationSize = 1)
	private Long   	id;
	
	@Column(name = "c_guid")
	private String 	guid;
	
	@Column(name = "c_nombre")
	private String 	nombre;
	
	@Column(name = "c_nombre_original")
	private String 	nombreOriginal;
	
	@Column(name = "c_formato")
	private String 	formato;
	
	@Column(name = "c_peso")
	private String 	peso;
	
	@Column(name = "c_ruta")
	private String 	ruta;
	
	@Column(name = "c_codigo_centro_computo")
	private String 	codigoCc;
	
	@Column(name = "n_documento_electoral")
	private Integer documentoElectoral;
	
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
