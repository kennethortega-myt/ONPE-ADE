package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;

@Data
public class FiltroAvanceMesaDto {

    private Long   idProceso;
    private Long   idAmbito;
    private String codigoEleccion;
    @Alphanumeric
    private String codigoAmbitoElectoral;
    private String ambitoElectoral;
    private Long   idCentroComputo;
    @Alphanumeric
    private String codigoCentroComputo;
    private String centroComputo;
    @Alphanumeric
    private String departamento;
    @Alphanumeric
    private String provincia;
    @Alphanumeric
    private String distrito;
    private Long   idUbigeo;
    @Alphanumeric
    private String mesa;
    private String proceso;
    private String eleccion;
    @Alphanumeric
    private String schema;
    private Integer idEleccion;
    @Alphanumeric
    private String usuario;
    private Integer preferencial;

    public void validarCamposNecesarios() {
        if (!tieneMesa()) {
            if ((esEleccionDiputado() || esEleccionSenadoresDistritoMultiple()) && !esCodigoExcluido()) {
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
