package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.orc.entities.TabAutorizacion;
import pe.gob.onpe.scebackend.model.orc.repository.TabAutorizacionRepository;
import pe.gob.onpe.scebackend.model.service.ITabAutorizacionTransactionalHelper;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TabAutorizacionTransactionalHelper implements ITabAutorizacionTransactionalHelper {

    private final TabAutorizacionRepository tabAutorizacionRepository;

    public TabAutorizacionTransactionalHelper(TabAutorizacionRepository tabAutorizacionRepository) {
        this.tabAutorizacionRepository = tabAutorizacionRepository;
    }

    @Override
    @Transactional("locationTransactionManager")
    public TabAutorizacion save2(TabAutorizacion tab) {
        try {
            return this.tabAutorizacionRepository.save(tab);
        }catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    @Transactional("locationTransactionManager")
    public TabAutorizacion registrarAutorizacion(String usuario, String tipo, String detalle) {
        try {
            TabAutorizacion tabAutorizacion = new TabAutorizacion();
            tabAutorizacion.setEstadoAprobacion(ConstantesComunes.ESTADO_PENDIENTE);
            tabAutorizacion.setTipoAutorizacion(tipo);
            tabAutorizacion.setNumeroAutorizacion(1L);
            tabAutorizacion.setAutorizacion(1);
            tabAutorizacion.setCodigoCentroComputo("");
            tabAutorizacion.setDetalle(detalle);
            tabAutorizacion.setFechaCreacion(new Date());
            tabAutorizacion.setUsuarioCreacion(usuario);
            tabAutorizacion.setActivo(ConstantesComunes.ACTIVO);
            return  this.tabAutorizacionRepository.save(tabAutorizacion);
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    @Transactional("locationTransactionManager")
    public AutorizacionNacionResponseDto recibirAutorizacion(String usuario, String tipoAutorizacion) {
        AutorizacionNacionResponseDto response = new AutorizacionNacionResponseDto();
        try{
            Optional<TabAutorizacion> validaAutorizacion = this.validaAutorizacion(usuario, tipoAutorizacion);
            if(validaAutorizacion.isPresent()) {
                TabAutorizacion tabAutorizacionObj = validaAutorizacion.get();
                response.setSolicitudGenerada(Boolean.TRUE);
                if (tabAutorizacionObj.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_APROBADO)){
                    response.setAutorizado(Boolean.TRUE);
                    response.setMensaje("Acceso autorizado");
                    tabAutorizacionObj.setEstadoAprobacion(ConstantesComunes.ESTADO_EJECUTADA);
                    TabAutorizacion aut = this.tabAutorizacionRepository.save(tabAutorizacionObj);
                    response.setIdAutorizacion(aut.getId().toString());
                }else{
                    response.setAutorizado(Boolean.FALSE);
                    response.setMensaje("Existe una solicitud pendiente que fue enviada a nación para aprobar el ingreso al módulo, espere por favor.");
                }
            }else{
                //aqui entra cuando no tiene una solicitud pendiente ni aprobada
                response.setSolicitudGenerada(Boolean.FALSE);
                response.setMensaje("No cuenta con ninguna solicitud pendiente de aprobación");
            }
            return response;
        }catch (Exception e) {
            throw new GenericException("Error en el recibir autorizacion");
        }

    }

    private Optional<TabAutorizacion> validaAutorizacion(String usr, String tipoAutorizacion) {
        List<String> estadosBuscados = new ArrayList<>();
        estadosBuscados.add(ConstantesComunes.ESTADO_APROBADO);
        estadosBuscados.add(ConstantesComunes.ESTADO_PENDIENTE);
        return this.tabAutorizacionRepository.findFirstByEstadoAprobacionInAndUsuarioCreacionAndTipoAutorizacionAndActivo(
                estadosBuscados, usr, tipoAutorizacion, ConstantesComunes.ACTIVO);
    }

}
