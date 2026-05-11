package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AvanceMesaMesaDto {
    private String ambito;
    private String centroComputo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String localVotacion;
    private String numeroMesa;
    private String fechaHora;
    private String copia;
    private Long cv;
    private Long cantidadElectoresHabiles;
    private String estado;
    private Long totalVotos;
    private List<AvanceMesaVotoDto> votos;
}
