package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteResultadosDto {
    private String distritoElectoral;
    private String descripcionDistritoElectoral;
    private String descripcionAgrupacionPolitica;
    private String votosValidos;
    private String division;
    private String cocienteObtenido;
    private String fechaProceso;
    private String observacionCifra;
    private String escanioObtenidos;
    private String cantidadCurules;
    private String factorCifra;
    private String estadoCifra;
    private Integer ubicacionAgrupacionPolitica;
    private String estadoDistritoElectoral;
    private String agrupacionPolitica;

}
