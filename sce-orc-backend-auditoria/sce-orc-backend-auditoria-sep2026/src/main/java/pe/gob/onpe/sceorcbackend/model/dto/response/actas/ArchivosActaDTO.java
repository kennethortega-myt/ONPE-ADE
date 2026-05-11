package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivosActaDTO {
    private Long actaId;
    private Long idArchivoEscrutinio;
    private Long idArchivoInstalacion;
    private Long idArchivoSufragio;
    private Long idArchivoInstalacionSufragio;
    private Integer codigoSolucionTecnologica;
    private String descSolucionTecnologica;
    private Integer codigoTipoTransmision;
    private String descripcionTipoTransmision;
}
