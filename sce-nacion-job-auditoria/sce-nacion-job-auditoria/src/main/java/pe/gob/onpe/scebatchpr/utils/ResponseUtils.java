package pe.gob.onpe.scebatchpr.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.gob.onpe.scebatchpr.dto.PrUpdatedDto;
import pe.gob.onpe.scebatchpr.dto.TramaSceResponse;
import pe.gob.onpe.scebatchpr.dto.TramaVistaDetalleResponse;
import pe.gob.onpe.scebatchpr.enums.EstadoEnum;

public class ResponseUtils {

	static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);
	
	private ResponseUtils() {}
	
	public static List<PrUpdatedDto> buildResponse(TramaSceResponse response){
		logger.info("Se obtuvo los datos de la respuesta del servicio (response): {}", response);
		List<PrUpdatedDto> updatedList = null;
		PrUpdatedDto updated = null;
		if(response!=null 
				&& response.getData()!=null 
				&& response.getData().getDetalle()!=null) {
			updatedList = new ArrayList<>();
			for(TramaVistaDetalleResponse detalle:response.getData().getDetalle()) {
				if(detalle.getFilas()!=null) {
					updated = new PrUpdatedDto();
					updated.setIdTransferencia(detalle.getIdTransferencia());
					updated.setEstado(detalle.isRecibido() ? EstadoEnum.CONFIRMADO.getValor(): EstadoEnum.SIN_CONFIRMAR.getValor());
					updated.setMensaje(detalle.getMensaje());
					updatedList.add(updated);
				}
			}
		}
		return updatedList;
	}
	
}
