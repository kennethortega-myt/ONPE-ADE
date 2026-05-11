package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReprocesarMesaResponseDto implements Serializable {
    private MesaDTO mesa;
    private List<TipoDocumentoReprocesarMesaDto> tipoDocumentos;
}
