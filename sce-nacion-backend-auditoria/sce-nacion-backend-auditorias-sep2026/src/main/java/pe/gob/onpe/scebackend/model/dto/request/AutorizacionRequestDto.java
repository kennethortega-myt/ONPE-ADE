package pe.gob.onpe.scebackend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AutorizacionRequestDto {
    Long idAutorizacion;

    String tipoAutorizacion;

    String descTipoAutorizacion;

    String codigoCentroComputo;

    String nombreCentroComputo;

    String detalle;
}
