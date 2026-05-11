package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotosEmpateResultadoDto {
    private String tipoEleccion;
    private String descripcionTipoEleccion;
    private String distritoElectoral;
    private String descripcionDistritoElectoral;
    private String agrupacionPolitica;
    private String descripcionAgrupacionPolitica;
    private String votosValidos;
    private String estadoResolucion;
    private String numeroResolucion;
    private String escanosObtenidos;
    private String escanosResolucion;
    private String descripcionEstado;
}
