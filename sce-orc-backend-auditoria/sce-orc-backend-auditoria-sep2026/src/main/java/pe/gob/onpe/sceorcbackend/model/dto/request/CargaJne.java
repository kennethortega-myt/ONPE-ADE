package pe.gob.onpe.sceorcbackend.model.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CargaJne {
    private String fechaCarga;
    private String idOdpe;
    private String txOdpe;
    private String numeroOficio;
    private List<RecepcionJne> recepciones;
}
