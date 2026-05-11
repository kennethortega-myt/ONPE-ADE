package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Data;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;

@Data
public class FiltroComparacionDigitacionAutomaticaDto {
    private Long idProceso;
    private Integer idAmbito;

    private String centroComputo;
    private String centroComputoCod;
    private String eleccion;
    private Integer idCentroComputo;
    private Integer idEleccion;

    private String acta;
    private Integer codOdpe;
    private String odpe;
    @Alphanumeric
    private String mesa;
    private String proceso;
    private String ubigeo;
    private String ambito;
    private String usuario;
    private String esquema;
    @Alphanumeric
    private String departamento;
    @Alphanumeric
    private String provincia;
    @Alphanumeric
    private String distrito;

    public void validarCamposNecesarios() {
        if (!tieneMesa()) {
            validarDepartamento();
            validarProvincia();
        }
    }

    private void validarDepartamento() {
        if (ConstantesReportes.CODIGO_UBIGEO_NACION.equals(departamento)) {
            throw new IllegalArgumentException("Se debe seleccionar un departamento");
        }
    }

    private void validarProvincia() {
        if (ConstantesReportes.CODIGO_UBIGEO_NACION.equals(provincia)) {
            throw new IllegalArgumentException("Se debe seleccionar un provincia");
        }
    }

    private boolean tieneMesa() {
        if (mesa == null || mesa.isEmpty()){
            return false;
        }
        return true;
    }
}
