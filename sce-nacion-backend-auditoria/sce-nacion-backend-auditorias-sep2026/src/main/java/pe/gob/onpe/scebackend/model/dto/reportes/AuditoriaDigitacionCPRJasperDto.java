package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Data;

@Data
public class AuditoriaDigitacionCPRJasperDto {

    private String codOdpe;
    private String descOdpe;
    private String descCentCompu;
    private String departamento;
    private String provincia;
    private String distrito;
    private String cvas;
    private Integer ubicacionAgrupol;
    private String descAgrupol;
    private String votosSI;
    private String votosNO;
    private String votosBL;
    private String votosNL;
    private String votosIM;
    private String estadoActa;
    private String estadoActaDescripcion;
    private String estadoRes;
    private String estadoCompu;
    private String numActa;
    private String digitoChequeo;
    private String copiaActa;
    private String codCentCompu;

}
