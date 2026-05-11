package pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura;

import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetCatalogoEstructuraDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 803038864560082030L;

    private Integer id;
    private String columna;
    private String nombre;
    private Integer codigoI;
    private String codigoS;
    private Integer orden;
    private String tipo;
    private String informacionAdicional;
    private Integer obligatorio;
    private Integer activo;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private Integer catalogo;
 
    public DetCatalogoEstructuraDTO(Integer id, String columna, String nombre, Integer codigoI, String codigoS,
                                    String tipo, Integer orden, Integer activo, Integer catalogo) {
        this.id = id;
        this.columna = columna;
        this.nombre = nombre;
        this.codigoI = codigoI;
        this.codigoS = codigoS;
        this.orden = orden;
        this.tipo = tipo;
        this.activo = activo;
        this.catalogo = catalogo;

    }


}
