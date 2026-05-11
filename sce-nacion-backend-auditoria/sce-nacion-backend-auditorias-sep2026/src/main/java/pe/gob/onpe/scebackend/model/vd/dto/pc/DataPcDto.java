package pe.gob.onpe.scebackend.model.vd.dto.pc;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataPcDto implements Serializable {
	
	private static final long serialVersionUID = 6402486805398315202L;
	private String usuario;
	private Integer proceder;
	private String resultado;
	private List<DataReportePcDto> data;
	
}
