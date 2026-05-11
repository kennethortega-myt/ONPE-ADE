package pe.gob.onpe.scebackend.model.dto.request;

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
public class AutorizacionFilterRequestDto {
    private String centroComputo;
    private String tipoAutorizacion;
    private String descripcionDetalle;
    private String estadoAprobacion;
}