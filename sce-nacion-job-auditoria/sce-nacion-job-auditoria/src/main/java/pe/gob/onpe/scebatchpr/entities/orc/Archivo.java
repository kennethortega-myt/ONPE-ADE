package pe.gob.onpe.scebatchpr.entities.orc;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_archivo")
public class Archivo implements Serializable  {

	private static final long serialVersionUID = -5153621057142144304L;

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
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "n_estado_transmision")
	private Integer estadoTransmision;
	
	@Column(name = "n_documento_electoral")
	private Integer documentoElectoral;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date	fechaModificacion;

	
}
