package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json;


import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@Data
public class DetMmRectanguloPaginaData implements Serializable {

	private static final long serialVersionUID = -5770805462126919189L;

	@JsonProperty("pagina")
    private Integer pagina;

	@JsonProperty("archivo_pagina")
    private Long archivoPagina;

	@JsonProperty("archivo_observacion")
    private Long archivoObservacion;

	@JsonProperty("secciones")
    private List<DetMmRectanguloSeccionData> secciones;

}
