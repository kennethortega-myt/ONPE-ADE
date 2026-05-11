package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReporteAvanceMesaPorMesaRevocatoriaDto {
    private String codigoAmbitoElectoral;
    private String nombreAmbitoElectoral;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
    private String nombreNivelUbigeoUno;
    private String nombreNivelUbigeoDos;
    private String nombreNivelUbigeoTres;
    private String nombreLocalVotacion;
    private String mesa;
    private String copia;
    private Short electoresHabiles;
    private Short cv;
    private Date fechaProcesamiento;
    private String estadoActa;
    private String digitadorUno;
    private String digitadorDos;
    private Short posicion;
    private String autoridad;
    private Short votosSi;
    private Short votosNo;
    private Short votosBlanco;
    private Short votosNulos;
    private Short votosImpugnados;
    private Short votosTotal;
}
