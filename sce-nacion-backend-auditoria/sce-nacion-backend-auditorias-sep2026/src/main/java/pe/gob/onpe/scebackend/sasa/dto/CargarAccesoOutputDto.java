package pe.gob.onpe.scebackend.sasa.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



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
