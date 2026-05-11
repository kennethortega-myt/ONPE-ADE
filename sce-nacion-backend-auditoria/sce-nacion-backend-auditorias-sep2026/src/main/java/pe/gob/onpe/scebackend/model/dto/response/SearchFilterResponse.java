package pe.gob.onpe.scebackend.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchFilterResponse<T> {

  private List<T> list;

  private Integer size;

  private Integer page;

  private Long total;

  private Integer totalPages;
}
