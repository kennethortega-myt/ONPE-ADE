package pe.gob.onpe.scebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MiembrosMesaDto {
    private String numeroMesa;
    private String documentoIdentidad;
    private Long ubigeo;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Long idTipoDocumento;
    private Long codigoCargo;
}