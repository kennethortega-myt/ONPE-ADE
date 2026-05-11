package pe.gob.onpe.scebatchpr.config;

import static org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValueMappingStrategy.RETURN_NULL;

import org.mapstruct.MapperConfig;

@MapperConfig(componentModel = "spring", nullValueCheckStrategy = ALWAYS, nullValueMappingStrategy = RETURN_NULL, collectionMappingStrategy = ADDER_PREFERRED)
public class DefaultConfigMapper {
	
}
