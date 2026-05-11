package pe.gob.onpe.scebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EleccionResultadoDto {
   private String cCodigoAmbitoElectoral;
    private String cNombreAmbitoElectoral;
    private String cCodigoCentroComputo;
    private String cNombreCentroComputo;
    private String cCodigoUbigeo;
    private String cDepartamento;
    private String cProvincia;
    private String cDistrito;
    private String cDocumentoIdentidad;
    private String cNombresApellidos;
    private Long nVotos;
    private Long nVotoOpcionSi;
    private Long nVotoOpcionNo;
    private Long nVotoOpcionBlanco;
    private Long nVotoOpcionNulo;
    private Long nVotoOpcionImpugnados;
    private Long nCiudadanosVotaron;
    private Long nElectoresHabiles;
    private Long nMesasAInstalar;
    private Long nMesasInstaladas;
    private Long nMesasNoInstaladas;
    private Long nMesasPorProcesar;
    private Long nMesasHabiles;
    private Long nActasProcesadas;
    private Long nEstadoContabilidadImpugnada;
    private Long nEstadoSolicitudNulidad;
    private Long nEstadoErrorMaterial;
    private Long nEstadoIlegible;
    private Long nEstadoSinDatos;
    private Long nEstadoOtrasObservaciones;
    private Long nEstadoContabilizadaNormal;
    private Long nEstadoNoInstalada;
    private Long nEstadoEnDigitacion;
    private Long nEstadoPendiente;
    private Long nEstadoExtraviada;
    private Long nEstadoContabilizadaAnulada;
    private Long nEstadoIncompleta;
    private Long nEstadoSinFirma;
    private Long nEstadoSiniestrada;
}