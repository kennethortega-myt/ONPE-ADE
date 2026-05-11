package pe.gob.onpe.scebackend.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginaOptDto<T> {

	private List<T> data;
	private boolean next;
	private Long lastId;

}
