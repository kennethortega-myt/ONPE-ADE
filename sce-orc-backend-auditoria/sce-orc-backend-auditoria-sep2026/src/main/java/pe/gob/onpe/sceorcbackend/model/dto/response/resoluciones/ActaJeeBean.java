package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;

import java.util.List;

@Data
public class ActaJeeBean {
    private String total;
    private String totalPendientes;
    private String totalNormales;
    private String totalObservadas;
    private String totalEnviadasJne;
    private List<ActaBean> actas;
}