package pe.gob.onpe.scebackend.model.orc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
public class UbigeoDestructurado implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_ubigeo_pk")
	private Long id;

	@Column(name = "c_nombre")
    private String nombre;
	
	
}
