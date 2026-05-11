package pe.gob.onpe.sceorcbackend.firma.service.impl;

import java.io.IOException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.firma.service.FirmaActaAprobadaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

@Service
public class FirmaActaAprobadaServiceImpl implements FirmaActaAprobadaService{
    
    Logger logger = LoggerFactory.getLogger(FirmaActaAprobadaServiceImpl.class);

    private final AsynServicioFirma asynServicioFirma1;
    private final AsynServicioFirma asynServicioFirma2;
    
    public FirmaActaAprobadaServiceImpl(
            AsynServicioFirma asynServicioFirma1,
            AsynServicioFirma asynServicioFirma2){
        this.asynServicioFirma1 = asynServicioFirma1;
        this.asynServicioFirma2 = asynServicioFirma2;
    }
    
    public void firmar(Acta acta, String usuario){
    
        Long actaId = acta.getId();
        Long archivoIdInstalacionSufragio = acta.getArchivoInstalacionSufragio().getId();
        Long archivoIdEscrutinio = acta.getArchivoEscrutinio().getId();
              
        try {
            logger.info("Inicia la tarea asincrona de firma del acta de instalación y sufragio.");
            this.asynServicioFirma1.procesarArchivoAsync(actaId, archivoIdInstalacionSufragio, usuario,ConstantesComunes.ACTA_INSTALACION_SUFRAGIO_FIRMA);
            
            logger.info("Inicia la tarea asincrona de firma del acta de escrutinio.");
            this.asynServicioFirma2.procesarArchivoAsync(actaId, archivoIdEscrutinio, usuario, ConstantesComunes.ACTA_ESCRUTINIO_FIRMA);
            
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FirmaActaAprobadaServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
