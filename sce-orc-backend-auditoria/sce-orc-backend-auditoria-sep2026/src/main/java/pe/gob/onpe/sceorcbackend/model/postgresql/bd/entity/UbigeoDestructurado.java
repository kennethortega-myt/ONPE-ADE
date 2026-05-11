package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
public class UbigeoDestructurado {

	@Id
	@Column(name = "n_ubigeo_pk")
	private Long id;

	@Column(name = "c_nombre")
    private String nombre;
	
	
}
