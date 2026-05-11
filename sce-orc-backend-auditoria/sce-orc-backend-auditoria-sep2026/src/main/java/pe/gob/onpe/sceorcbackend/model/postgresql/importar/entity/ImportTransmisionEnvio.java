package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;



import java.util.Date;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "tab_transmision_envio")
public class ImportTransmisionEnvio {

	@Id
	@Column(name = "n_transmision_envio_pk")
	private Long 	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_centro_computo", referencedColumnName = "n_centro_computo_pk", nullable = false)
	private ImportCentroComputo centroComputo;

	@Column(name = "n_acta")
	private Long idActa;
	
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
	public void prePersist() {
		this.fechaCreacion = new Date();
	}
	
}
