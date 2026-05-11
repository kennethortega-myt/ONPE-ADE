package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmDocElectoralDto {

	private Long 	id;
	private Long    idPadre;
	private String  nombre;
	private String 	abreviatura;
	private Integer tipoImagen;
	private Integer escanerAmbasCaras;
	private Integer tamanioHoja;
	private Integer multipagina;
	private Integer visible;
    private String codigoBarraPixelTopX;
    private String codigoBarraPixelTopY;
    private String codigoBarraPixelBottomX;
    private String codigoBarraPixelBottomY;
    private String codigoBarraCoordenadaRelativaTopX;
    private String codigoBarraCoordenadaRelativaTopY;
    private String codigoBarraCoordenadaRelativaBottomX;
    private String codigoBarraCoordenadaRelativaBottomY;
    private String codigoBarraWidth;
    private String codigoBarraHeight;
    private Integer codigoBarraOrientacion;
    private Integer configuracionGeneral;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;

}
