package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteComparacionOmisosAusentismoDto {
    private String codiDesCompu;
    private String codiDesOdpe;
    private String codigoUbigeo;
    private String descDepartamento;
    private String descProvincia;
    private String descDistrito;
    private String codiLocal;
    private String nombLocal;
    private String dirLocal;
    private String numeroMesa;
    private Integer nroMesaOmisa;
    private Integer votantesHabiles;
    private Integer votantesAusentes;
    private Integer votantesOmisos;
    private boolean conDiferencias;
}
