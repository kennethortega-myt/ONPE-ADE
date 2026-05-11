package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class ActaBeanRevocatoria implements Serializable {


	private static final long serialVersionUID = 1613947531099142713L;
	private Integer index;
    private String lote;
    private String mesa;
    private String copia;
    private String autoridad;
    private String eleccion;
    private String errorMaterial;
    private String tipoErrorM;
    private String votosImpugnados;
    private String ilegibilidad;
    private String actasIncompletas;
    private String solNulidad;
    private String actaSinDatos;
    private String actaSinFirma;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActaBeanRevocatoria actaBean = (ActaBeanRevocatoria) o;
        return Objects.equals(mesa, actaBean.mesa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mesa);
    }
}
