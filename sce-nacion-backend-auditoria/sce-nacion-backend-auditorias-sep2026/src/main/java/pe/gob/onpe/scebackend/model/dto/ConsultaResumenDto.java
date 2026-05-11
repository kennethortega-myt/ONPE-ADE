package pe.gob.onpe.scebackend.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultaResumenDto {
    String cifraRepartidora;
    String totalEscanios;
    String horaProceso;
    String estado;
    String estadoDistritoElectoral;
    Long totalVotosValidos;
    String vallaPorcentajeVotos;
    String vallaNumeroMiembros;
}
