package pe.gob.onpe.sceorcbackend.model.dto.response.mesas;

import lombok.Data;

import java.util.List;

@Data
public class DigitizationListMesasItem {
    private Long mesaId;//900661
    private String mesa;//000001
    private String estado;//COMPLETA - PARCIAL
    private String descEstado;
    private String paginas;//paginas digitalizadas  30 de 30  |   17 de 30
    private String filePdfId;//id del archivo PDF
    private String filePdfStatus;//status del archivo   'Validado automaticamente'   |        'redigitalizar'
    private List<String> listaPaginas;
}
