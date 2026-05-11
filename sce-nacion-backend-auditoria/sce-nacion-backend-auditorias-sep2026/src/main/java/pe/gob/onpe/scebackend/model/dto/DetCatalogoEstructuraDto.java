package pe.gob.onpe.scebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DetCatalogoEstructuraDto {

	private Integer	id;
	private String  columna;
	private String  nombre;
	private Integer	codigoI;
	private String  codigoS;
	private Integer	orden;
	private String 	tipo;
	private String  informacionAdicional;
	private Integer obligatorio; 	
	private Integer activo;
	private String 	usuarioCreacion;
	private Date fechaCreacion;
	private String	UsuarioModificacion;
	private Date fechaModificacion;
    private Integer catalogo;

    public DetCatalogoEstructuraDto(
            String columna, String nombre, Integer codigoI, String codigoS,
            Integer orden, Integer activo
    ) {
        this.columna = columna;
        this.nombre = nombre;
        this.codigoI = codigoI;
        this.codigoS = codigoS;
        this.orden = orden;
        this.activo = activo;
    }
	
}
