package pe.gob.onpe.sceorcbackend.model.dto;

import pe.gob.onpe.sceorcbackend.model.postgresql.dto.ArchivosRectanguloDto;

import lombok.Data;

@Data
public class MiembroMesaEscrutinioSeccionesDto {

  private ArchivosRectanguloDto archivoPresidente;
  private ArchivosRectanguloDto archivoSecretario;
  private ArchivosRectanguloDto archivoTercerMiembro;

}
