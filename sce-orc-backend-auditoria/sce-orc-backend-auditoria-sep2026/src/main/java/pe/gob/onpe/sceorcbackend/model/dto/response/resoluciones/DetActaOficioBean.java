package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;


import java.io.Serializable;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabActaFormato;

@Data
public class DetActaOficioBean implements Serializable {

	private static final long serialVersionUID = -6963838939228080618L;
	
	private Acta actaPlomo;
	private ActaCeleste actaCeleste;
	private CabActaFormato cabActaFormato;

}
