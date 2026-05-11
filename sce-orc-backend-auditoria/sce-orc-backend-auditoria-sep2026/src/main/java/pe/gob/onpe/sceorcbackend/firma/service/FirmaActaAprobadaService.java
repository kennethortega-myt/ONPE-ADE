package pe.gob.onpe.sceorcbackend.firma.service;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;

public interface FirmaActaAprobadaService {
    
        void firmar(Acta acta, String usuario);
}
