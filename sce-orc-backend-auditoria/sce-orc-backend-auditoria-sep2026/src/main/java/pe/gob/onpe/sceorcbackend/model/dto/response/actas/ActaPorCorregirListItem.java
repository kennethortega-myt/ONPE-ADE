package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
@Data
@NoArgsConstructor
public class ActaPorCorregirListItem implements Serializable {

	@Serial
    private static final long serialVersionUID = -2760988150832395149L;
	private Long actaId;
    private String mesa;
    private String copia;
    private String digitoChequeo;
    private String eleccion;
    private String codigoEleccion;
    private Integer cantidadColumnas;
    private String ubigeo;
    private Long electoresHabiles;
    private Long acta1FileId;
    private Long acta2FileId;

    public ActaPorCorregirListItem(Long id, String codigo, String numeroCopia, String digitoChequeoEscrutinio, String nombre) {
        this.actaId = id;
        this.mesa = codigo;
        this.copia = numeroCopia;
        this.digitoChequeo = digitoChequeoEscrutinio;
        this.eleccion = nombre;
    }
}
