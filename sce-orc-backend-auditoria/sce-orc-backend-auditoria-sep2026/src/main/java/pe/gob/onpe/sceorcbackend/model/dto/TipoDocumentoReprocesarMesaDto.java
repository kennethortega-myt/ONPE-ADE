package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoDocumentoReprocesarMesaDto implements Serializable {
    private String codigo;
    private String nombre;
    private boolean reprocesar;
}
