package pe.gob.onpe.scebackend.model.dto;

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
public class PaginaDto<T>  {

    private long totalRegistros;
    private long totalPaginas;
    private String paginaSiguiente;
    private String paginaAnterior;
    private boolean next;
    private Iterable<T> data;
	
}
