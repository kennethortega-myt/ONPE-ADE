package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(exclude = { "tipoTransmision", "agrupacionesPoliticas" })
public class ActaBean implements Serializable {

    private static final long serialVersionUID = 6512375289441453889L;
    private Integer index;
    private Long actaId;
    private Long mesaId;
    private String idArchivoEscrutinio;
    private String idArchivoInstalacionSufragio;
    private Integer cantidadColumnas;
    private String resolucionId;
    private String mesa;
    private String copia;
    private String eleccion;
    private String codigoEleccion;
    private String codigoProceso;
    private String estadoActa;
    private String estadoDigitalizacion;
    private String estadoComputo;
    private String estadoResolucion;
    private String estadoMesa;
    private String estadoDigitacion;
    private String tipoResolverExtraviadaSiniestrada;// anulada encontrada
    private String descripcionEstadoActa;
    private String descripcionEstadoMesa;
    private Long electoresHabiles;
    private String cvas;
    private String ubigeo;
    private String localVotacion;
    private String fecha;
    private String imagenEscrutinio;
    private String horaEscrutinio;
    private String imagenInstalacion;
    private String horaInstalacion;
    private String errorMaterial;
    private String tipoErrorM;
    private String votosImpugnados;
    private String ilegibilidad;
    private String tipoIlegible;
    private String detalleIlegible;
    private String actasIncompletas;
    private String solNulidad;
    private String actaSinDatos;
    private String actaSinFirma;
    private String observacion;
    private String observacionesJNE;
    private String tipoLote;
    private String extraviada;
    private String siniestrada;
    private String obsMesa;
    private String tipoComboNulos;
    private boolean solicitudNulidad;
    private boolean sinFirma;

    /*
     * 1 = STAE transmitida 2 = STAE no transmitida 3 = STAE Contingencia 4 =
     * Convencional
     */
    private Integer tipoTransmision;
    private List<AgrupolBean> agrupacionesPoliticas;
    private boolean staeIntegrada;
    private String idArchivoEscrutinioFirmado;
    private String idArchivoInstalacionFirmado;
    private String idArchivoSufragioFirmado;
    private boolean flujoAutomatizado;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ActaBean actaBean = (ActaBean) o;
        return Objects.equals(actaId, actaBean.actaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actaId);
    }

}
