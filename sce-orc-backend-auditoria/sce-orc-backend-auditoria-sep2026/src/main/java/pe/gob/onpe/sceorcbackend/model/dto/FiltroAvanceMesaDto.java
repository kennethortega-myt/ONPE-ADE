package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class FiltroAvanceMesaDto {

    private Long idProceso;
    private Integer idEleccion;
    private String codigoEleccion;
    private Long idAmbito;
    private Long idCentroComputo;
    @Alphanumeric
    private String departamento;
    @Alphanumeric
    private String provincia;
    private Long idUbigeo;
    @Alphanumeric
    private String mesa;
    @Alphanumeric
    private String codigoAmbitoElectoral;
    private String ambitoElectoral;
    @Alphanumeric
    private String codigoCentroComputo;
    private String centroComputo;
    @Alphanumeric
    private String distrito;
    private String proceso;
    private String eleccion;
    @Alphanumeric
    private String schema;
    @Alphanumeric
    private String usuario;
    private Integer preferencial;
    @Alphanumeric
    private String acronimo;

    public void validarCamposNecesarios() {
        if (!tieneMesa()) {
            if ((esEleccionDiputado() || esEleccionSenadoresDistritoMultiple()) ) {
                validarDepartamento();
            }
        }
    }

    private boolean esCodigoExcluido() {
        return ConstantesComunes.DIPUTADOS.equals(codigoEleccion);
    }

    private boolean tieneMesa() {
        if (mesa == null || mesa.isEmpty()){
            return false;
        }
        return true;
    }

    private boolean esEleccionDiputado() {
        return ConstantesComunes.COD_ELEC_DIPUTADO.equals(codigoEleccion);
    }

    private boolean esEleccionSenadoresDistritoMultiple() {
        return ConstantesComunes.COD_ELEC_SENADO_MULTIPLE.equals(codigoEleccion);
    }

    private void validarDepartamento() {
        if (ConstantesReportes.CODIGO_UBIGEO_NACION.equals(departamento)) {
            throw new IllegalArgumentException("Se debe seleccionar un departamento");
        }
    }



}
