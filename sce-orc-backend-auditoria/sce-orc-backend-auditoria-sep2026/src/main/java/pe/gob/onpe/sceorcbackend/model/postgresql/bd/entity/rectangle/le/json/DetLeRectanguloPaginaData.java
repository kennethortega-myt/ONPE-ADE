package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class DetLeRectanguloPaginaData implements Serializable {

	@Serial
    private static final long serialVersionUID = -7755047791684866798L;

	@JsonProperty("pagina")
    private Integer pagina;

    @JsonProperty("archivo_pagina")
    private Long archivoPagina;

    @JsonProperty("archivo_observacion")
    private Long archivoObservacion;

    @JsonProperty(value = "existe_observacion", defaultValue = ConstantesComunes.TEXT_FALSE)
    private Boolean existeObservacion;

    @JsonProperty("secciones")
    private List<DetLeRectanguloSeccionData> secciones;

}
