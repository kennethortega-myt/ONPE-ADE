package pe.gob.onpe.scebackend.model.dto;

import java.util.Objects;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrganizacionPoliticaReporteDto {

    private String departamento;
    private String provincia;
    private String distrito;
    private String codigoUbigeo;
    private String agrupacionPolitica;
    private String codigoAgrupol;
    private String eleccion;
    private String nombreEleccion;
    private String proceso;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizacionPoliticaReporteDto that = (OrganizacionPoliticaReporteDto) o;
        return Objects.equals(codigoAgrupol, that.codigoAgrupol);

    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoAgrupol);
    }
    
}
