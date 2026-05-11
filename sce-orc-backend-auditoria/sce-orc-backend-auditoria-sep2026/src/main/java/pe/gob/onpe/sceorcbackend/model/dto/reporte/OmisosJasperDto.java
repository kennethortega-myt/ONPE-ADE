package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;

@Data
public class OmisosJasperDto {

    private String codDescCC;
    private Integer totalMesas;
    private Integer mesasOmisas;
    private Integer votantes;
    private Integer omisos;
    private String codDescODPE;
    private String codUbigeo;
    private String descDepartamento;
    private String descProvincia;
    private String descDistrito;

}
