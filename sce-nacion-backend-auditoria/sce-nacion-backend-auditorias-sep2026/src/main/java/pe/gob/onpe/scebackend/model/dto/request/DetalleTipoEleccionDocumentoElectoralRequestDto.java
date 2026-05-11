package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;
import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;

@Data
public class DetalleTipoEleccionDocumentoElectoralRequestDto {
    
    private Integer id;
    private DatosGeneralesRequestDto tipoEleccion;
    
    private DatosGeneralesRequestDto documentoElectoral;
    
    private ArchivoDTO archivo;
    
    private Integer requerido;
    
    private Integer activo;

    private String rangoInicial;

    private String rangoFinal;

    private String digitoChequeo;

    private String digitoError;
}
