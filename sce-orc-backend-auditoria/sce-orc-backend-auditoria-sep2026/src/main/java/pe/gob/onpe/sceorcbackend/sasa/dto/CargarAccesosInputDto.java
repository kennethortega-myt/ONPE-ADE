package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class CargarAccesosInputDto {

    @NotNull(message = "El id del usuario no puede ser nulo")
    private Integer idUsuario;

    @NotNull(message = "El id del perfil no puede ser nulo")
    private Integer idPerfil;
}
