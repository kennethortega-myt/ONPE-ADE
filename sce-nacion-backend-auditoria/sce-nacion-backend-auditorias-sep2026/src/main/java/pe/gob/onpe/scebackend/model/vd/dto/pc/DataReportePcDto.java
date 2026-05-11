package pe.gob.onpe.scebackend.model.vd.dto.pc;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataReportePcDto implements Serializable {

	private static final long serialVersionUID = 3625478371813656824L;
	
	private String  idEleccion;
    private String  nombreEleccion;
    private Integer  actaProcesar;
    private Integer  actaPendiente;
    private Integer  actaProcesada;
    private Integer  actaJEEPendiente;
    private Integer  actaJEEResuelta;
    private Integer  actaJEEPendienteResuelta;
	
}
