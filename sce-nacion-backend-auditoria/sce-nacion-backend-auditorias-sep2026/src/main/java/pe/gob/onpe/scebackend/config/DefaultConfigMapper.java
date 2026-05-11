package pe.gob.onpe.scebackend.config;

import org.mapstruct.MapperConfig;

import static org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;

@MapperConfig(componentModel = "spring", nullValueCheckStrategy = ALWAYS, nullValueMappingStrategy = RETURN_NULL, collectionMappingStrategy = ADDER_PREFERRED)
public class DefaultConfigMapper {
}
