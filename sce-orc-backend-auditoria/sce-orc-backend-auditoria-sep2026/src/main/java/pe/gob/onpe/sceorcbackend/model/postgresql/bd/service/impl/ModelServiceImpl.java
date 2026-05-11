package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveModelo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ModeloService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

@Service
public class ModelServiceImpl implements ModeloService {

    Logger logger = LoggerFactory.getLogger(ModelServiceImpl.class);

    private final CabActaService cabActaService;

    public ModelServiceImpl(CabActaService cabActaService) {
        this.cabActaService = cabActaService;
    }

    @Override
    @Transactional
    public GenericResponse<Boolean> approveMesaModelo(DigitizationApproveModelo request) {

        try {
            Usuario usuario = this.cabActaService.getUsuarioService().findByUsername(request.getUsuario());
            if (usuario != null) {
                int asignadasActual = usuario.getActasAsignadas() == null ? 0 : usuario.getActasAsignadas();
                usuario.setActasAsignadas(asignadasActual + 1);
                this.cabActaService.getUsuarioService().save(usuario);
            } else {
                logger.info("Usuario no encontrado (flujo extranjero): {}", request.getUsuario());
            }

            Optional<Acta> optionalActa = this.cabActaService.getCabActaRepository().findById(request.getActaId());

            if (optionalActa.isPresent()) {
                Acta cabActa = optionalActa.get();
                cabActa.setAsignado(ConstantesComunes.ACTIVO);
                cabActa.setUsuarioAsignado(request.getUsuario());
                cabActa.setUsuarioModificacion(request.getUsuario());
                cabActa.setFechaModificacion(new Date());
                this.cabActaService.getCabActaRepository().save(cabActa);

                guardarDetAccion(cabActa.getId(), request.getCodigoCc(), request.getUsuario(), new Date(),
                        ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
                        ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_DIGTAL, 2);

                DigitizationApproveMesaRequest digitizationApproveMesaRequest = new DigitizationApproveMesaRequest();
                digitizationApproveMesaRequest.setEstado(request.getEstado());
                digitizationApproveMesaRequest.setActaId(request.getActaId());
                digitizationApproveMesaRequest.setFileId1(request.getFileId1());
                digitizationApproveMesaRequest.setFileId2(request.getFileId2());

                this.cabActaService.approveMesa(digitizationApproveMesaRequest, request.getUsuario(),
                        request.getAbrevProceso(), request.getCodigoCc());
                return new GenericResponse<>(true, "Se procesó correctamente el control automático.", true);

            } else {
                return new GenericResponse<>(false, "El acta no existe..", true);
            }

        } catch (Exception e) {
            logger.error("Error: ", e);
            // Marcar rollback manualmente
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new GenericResponse<>(false, e.getMessage(), false);
        }
    }

    private void guardarDetAccion(Long idActa, String codigoCentroComputo, String usuario, Date fecha, String tiempo,
            String accion, int orden) {
        DetActaAccion detActaAccion = DetActaAccion.builder().fechaAccion(fecha)
                .codigoCentroComputo(codigoCentroComputo).usuarioAccion(usuario).tiempo(tiempo).activo(1).accion(accion)
                .usuarioCreacion(usuario).acta(new Acta(idActa)).orden(orden).build();
        this.cabActaService.getDetActaAccionService().save(detActaAccion);
    }

}
