package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReturnMonitoreoActas {
    private String total;
    private String totalNormales;
    private String totalObservadas;
    private String totalEnviadasJne;
    private String totalDevueltasJne;
    private List<MonitoreoListActaItem> listActaItems;
}
