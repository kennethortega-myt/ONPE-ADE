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
import pe.gob.onpe.scebackend.model.entities.DatosAuditoria;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "mae_agrupacion_politica_real")
public class AgrupacionPoliticaReal extends DatosAuditoria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7770272483020123053L;

	@Id
    @Column(name = "n_agrupacion_politica_real_pk")
	private Long   	id;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@Column(name = "c_descripcion")
	private String  descripcion;
	
	@Column(name = "n_tipo_agrupacion_politica")
	private Long	tipoAgrupacionPolitica;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "c_ubigeo_maximo")
	private String  ubigeoMaximo;
	
	@Column(name = "n_activo")
	private Integer activo;
	
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
