package pe.gob.onpe.scebackend.model.exportar.orc.dto;


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
@Builder
@ToString
public class AdmDocElectoralExportDto {

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
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
