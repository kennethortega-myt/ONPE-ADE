package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;
import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.DocumentoElectoralRequestDto;

@Data
public class DetalleTipoEleccionDocumentoElectoralResponseDto {

    private Integer id;
    private DatosGeneralesRequestDto tipoEleccion;

    private DocumentoElectoralRequestDto documentoElectoral;

    private ArchivoDTO archivo;

    private Integer requerido;

    private Integer activo;

    private String archivoBase64;

    private String rangoInicial;

    private String rangoFinal;

    private String digitoChequeo;

    private String digitoError;

}
