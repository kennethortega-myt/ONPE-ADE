package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
public class ReporteAvanceMesaReportePreferencialDto extends ReportePreferencialBaseDto {
    private Integer idAmbitoElectoral;
    private String codigoAmbitoElectoral;
    private String nombreAmbitoElectoral;
    private Integer idCentroComputo;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
    private Integer idDistritoElectoral;
    private String codigoDistritoElectoral;
    private String nombreDistritoElectoral;
    private Integer idUbigeo;
    private String codigoUbigeo;
    private String nombreUbigeoNivelUno;
    private String nombreUbigeoNivelDos;
    private String nombreUbigeoNivelTres;
    private Integer idLocalVotacion;
    private String codigoLocalVotacion;
    private String nombreLocalVotacion;
    private Integer idMesa;
    private String mesa;
    private Long idActa;
    private String numeroCopia;
    private Integer electoresHabiles;
    private String cvas;
    private String estadoActa;
    private String descripcionEstadoActa;
    private Short posicion;
    private Integer idAgrupacionPolitica;
    private String descripcionAgrupacionPolitica;
    private String votos;
    private String fechaProcesamiento;
    private String verificador;
    private Boolean esAgrupacionPolitica;
}
