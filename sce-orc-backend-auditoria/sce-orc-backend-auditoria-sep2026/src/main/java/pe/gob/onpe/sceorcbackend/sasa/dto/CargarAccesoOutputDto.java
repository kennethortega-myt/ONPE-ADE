package pe.gob.onpe.sceorcbackend.sasa.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargarAccesoOutputDto {
    private List<LoginPerfilesOutputDto> perfiles;
    private List<LoginModulosOutputDto> modulos;
    private List<LoginOpcionesOutputDto> opciones;
    private List<LoginAccionesOutputDto> acciones;
    private LoginUsuarioOutputDto usuario;
}
