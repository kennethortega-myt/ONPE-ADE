package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

public interface IMigracionMapper<Dto, Entity> {

	Dto toDto(Entity entity);
	
}
