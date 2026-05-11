package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PadronImportadoDto {
    private long totalRegistros;
    private long totalPaginas;
    private String paginaSiguiente;
    private String paginaAnterior;
    private boolean next;
    private List<MaePadronDto> data;
}
