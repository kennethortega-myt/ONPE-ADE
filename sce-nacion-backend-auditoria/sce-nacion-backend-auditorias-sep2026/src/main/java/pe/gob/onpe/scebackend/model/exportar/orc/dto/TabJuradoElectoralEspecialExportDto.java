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
public class TabJuradoElectoralEspecialExportDto {


    private Integer id;
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
