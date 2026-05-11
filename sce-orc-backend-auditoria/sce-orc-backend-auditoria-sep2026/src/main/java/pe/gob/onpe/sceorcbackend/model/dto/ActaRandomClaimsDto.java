package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
@Data
public class ActaRandomClaimsDto {
    private Long idActa;
    private String numMesa;
    private String ubigeo;
    private String copia;
    private String esActaDeConsulado;
    private String departamento;
    private String provincia;
    private String distrito;
    private String localVotacion;
    private Long idDetUbigeoEleccion;
    private String idEleccion;
    private Long actaIdArchivoEscrutinio;
    private Long actaIdArchivoInstalacionSufragio;
}
