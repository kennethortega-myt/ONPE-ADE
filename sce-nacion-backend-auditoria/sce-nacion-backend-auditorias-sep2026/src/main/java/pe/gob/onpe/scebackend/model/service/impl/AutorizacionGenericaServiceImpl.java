package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.service.AutorizacionGenericaService;
import pe.gob.onpe.scebackend.model.service.IAutorizacionService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.impl.comun.AmbitoElectoralService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizacionGenericaServiceImpl implements AutorizacionGenericaService {

    private final IAutorizacionService autorizacionService;

    private final AmbitoElectoralService ambitoElectoralService;

    private final CentroComputoService centroComputoService;

    private final ITabLogTransaccionalService logService;

    @Override
    @Transactional(value = "locationTransactionManager")
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String tipoAutorizacion) {
        try {
            CentroComputo ccNacion = centroComputoService.getPadreNacion();

            AutorizacionNacionRequestDto requestDto = new AutorizacionNacionRequestDto();
            requestDto.setCc(ccNacion.getCodigo());
            requestDto.setUsuario(usuario);
            requestDto.setTipoAutorizacion(tipoAutorizacion);

            ResponseEntity<AutorizacionNacionResponseDto> response = this.autorizacionService
                    .recibirAutorizacion(requestDto);

            AutorizacionNacionResponseDto responseDto = new AutorizacionNacionResponseDto();
            responseDto.setAutorizado(response.getBody().isAutorizado());
            responseDto.setMensaje(response.getBody().getMensaje());
            responseDto.setSolicitudGenerada(response.getBody().isSolicitudGenerada());
            return responseDto;
        } catch (Exception e) {
            log.error("Error al obtener autorizacion", e);
            return null;
        }
    }

    @Override
    @Transactional(value = "locationTransactionManager")
    public Boolean solicitaAutorizacionNacion(String usuario, String tipoAutorizacion) {
        try {
            CentroComputo ccNacion = centroComputoService.getPadreNacion();
            AmbitoElectoral ambito = ambitoElectoralService.getPadreNacion();

            AutorizacionNacionRequestDto requestDto = new AutorizacionNacionRequestDto();
            requestDto.setCc(ccNacion.getCodigo());
            requestDto.setUsuario(usuario);
            requestDto.setTipoAutorizacion(tipoAutorizacion);

            ResponseEntity<GenericResponse> response = this.autorizacionService.crearSolicitudAutorizacion(requestDto);

            GenericResponse body = response.getBody();
            boolean isSucces = body != null && body.isSuccess();
            if (isSucces) {
                String mensaje = this.autorizacionService.generarDescripcionGenerica(requestDto);
                this.logService.registrarLog(
                        usuario,
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        this.getClass().getSimpleName(),
                        mensaje,
                        ambito.getCodigo(), ccNacion.getCodigo(),
                        ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                        ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            }

            return isSucces;
        } catch (Exception e) {
            log.error("Error al solicitar autorización", e);
            return false;
        }
    }

}
