package pe.gob.onpe.scebackend.model.stae.dto;

import java.util.List;

import lombok.Data;

@Data
public class DetalleActaDto {

	private String codigoAgrupacionPolitica;
    private Integer posicionAgrupacionPolitica;
    private Integer estadoAgrupacionPolitica;
    private String estadoErrorAritmetico;
    private Integer votos;
    private List<DetalleActaPreferencialDto> detalleActaPreferencial;
	
}
