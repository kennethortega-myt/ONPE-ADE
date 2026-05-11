package pe.gob.onpe.scebatchpr.repository.orc;

import java.util.List;

import pe.gob.onpe.scebatchpr.dto.VwArchivoEscrutinioSinFirmarDto;
import pe.gob.onpe.scebatchpr.dto.VwArchivoInstalacionSufragioSinFirmarDto;
import pe.gob.onpe.scebatchpr.dto.VwArchivoTransmisionDto;

public interface ArchivoTransmisionRepositoryCustom {

	List<VwArchivoTransmisionDto> listarArchivosPendientes();
	List<VwArchivoEscrutinioSinFirmarDto> listarArchivosEscrutinioSinFirmar();
	List<VwArchivoInstalacionSufragioSinFirmarDto> listarArchivosInstalacionSufragioSinFirmar();
	
}
