package pe.gob.onpe.scebackend.model.stae.dto;

import java.util.List;

import lombok.Data;

@Data
public class MesaElectoresRequestDto {

	private String numeroMesa;
    private List<ElectorDto> detalleListaElectores;
}
