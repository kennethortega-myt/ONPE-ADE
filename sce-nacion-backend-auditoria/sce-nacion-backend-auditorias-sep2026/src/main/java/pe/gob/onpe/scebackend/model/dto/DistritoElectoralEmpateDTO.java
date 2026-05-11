package pe.gob.onpe.scebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DistritoElectoralEmpateDTO {
    private String distritoElectoral;
    private String descripcionDistritoElectoral;
    private String tipoEleccion;
    private String descripcionTipoEleccion;
    private String votosValidos;
    private List<VotosEmpateResultadoDto> agrupacionesPoliticas;

    public DistritoElectoralEmpateDTO() {
        this.agrupacionesPoliticas = new ArrayList<>();
    }
}
