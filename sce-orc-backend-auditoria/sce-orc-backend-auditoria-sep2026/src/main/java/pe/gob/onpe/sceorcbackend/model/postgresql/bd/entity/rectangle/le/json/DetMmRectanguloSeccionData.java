package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json;


import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class DetMmRectanguloSeccionData implements Serializable {

	private static final long serialVersionUID = -8415480378932104810L;

	@JsonProperty("cargo")
    private Integer cargo;

	@JsonProperty("archivo_seccion")
    private Long archivoSeccion;

	@JsonProperty("huella")
    private Boolean huella;//dejarlo por si generarn huella

	@JsonProperty("firma")
    private Boolean firma;

	@JsonProperty("no_voto")
    private Boolean noVoto;//dejarlo por si generan un campo no votó

}
