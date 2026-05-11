package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TabJuradoElectoralEspecialDto {

	private Integer id;
    private Long idCentroComputo;
    private String codigoCentroComputo;
    private String nombre;
    private String idJee;
    private String direccion;
    private String apellidoPaternoRepresentante;
    private String apellidoMaternoRepresentante;
    private String nombresRepresentante;
    private Integer activo;
    private String usuarioCreacion;
    private String fechaCreacion;
    private String usuarioModificacion;
    private String fechaModificacion;
	
}
