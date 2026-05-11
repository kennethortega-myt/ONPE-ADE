package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class AvanceMesaReporteDto {
    private Long idActa;
    private String proceso;
    private String eleccion;
    private String ambito;
    private String ambitoElectoral;
    private String codigoAmbitoElectoral;
    private String centroComputo;
    private String codigoCentroComputo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String localVotacion;
    private String numeroMesa;
    private String fechaHora;
    private String copia;
    private String verificador;
    private String cvas;
    private Long cantidadElectoresHabiles;
    private String estado;
    private String etiquetaEstado;
    private String totalVotos;
    private Integer idAgrupacionPolitica;
    private String agrupacionPolitica;
    private Long posicion;
    private String votos;
    private String codigoUbigeo;
    
    //CPR
    private String descAgrupol;
    private String votosSI;
    private String votosNO;
    private String votosBL;
    private String votosNL;
    private String votosIM;
    private String codLocal;
    private String codUbigeo;
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvanceMesaReporteDto that = (AvanceMesaReporteDto) o;
        return Objects.equals(idAgrupacionPolitica, that.idAgrupacionPolitica);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAgrupacionPolitica);
    }
}
