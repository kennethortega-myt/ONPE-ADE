package pe.gob.onpe.scebackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.exeption.AuthTransmisionException;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.TokenValidadorService;
import pe.gob.onpe.scebackend.model.service.impl.TransmisionService;
import pe.gob.onpe.scebackend.utils.constantes.ConstanteTransmision;

@RestController
@Validated
@RequestMapping("/transmision-pc")
public class PuestaCeroCcController {

	Logger logger = LoggerFactory.getLogger(PuestaCeroCcController.class);

	@Autowired
	private IConfiguracionProcesoElectoralService confProcesoService;

	@Autowired
	private TokenValidadorService tokenValidadorService;

	@Autowired
	private TransmisionService transmisionService;

	@PatchMapping({ "/recibir-puesta-cero", "/recibir-puesta-cero/" })
	public ResponseEntity<TransmisionResponseDto> puestaCero(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestHeader("codigocc") String cc,
			@RequestBody TransmisionNacionRequestDto request) {

		TransmisionResponseDto response = new TransmisionResponseDto();

		try {

			boolean autenticacion = this.tokenValidadorService.validarToken(authorization, cc);

			if (!autenticacion) {
				throw new AuthTransmisionException("Token invalido");
			}

			String proceso = request.getProceso();
			String esquema = this.confProcesoService.getEsquema(proceso);
			Long idTransmision = request.getActasTransmitidas().getFirst().getIdTransmision();

			logger.info("esquema: {}", esquema);

			response.setIdTransmision(idTransmision);

			TransmisionDto acta = request.getActasTransmitidas().getFirst();

			response = transmisionService.recibirTransmisionPc(acta, esquema);

			if (response.isExitoso()) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}

		} catch (AuthTransmisionException e) {
			response.setExitoso(false);
			response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
			response.setMensaje("Token no autorizado");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		} catch (Exception e) {
			response.setExitoso(false);
			response.setMensaje(e.getMessage());
			response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
