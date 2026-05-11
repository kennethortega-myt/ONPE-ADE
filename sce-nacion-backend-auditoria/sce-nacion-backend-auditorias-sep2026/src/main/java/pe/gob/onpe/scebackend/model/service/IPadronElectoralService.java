package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.orc.entities.PadronElectoral;

public interface IPadronElectoralService {

    PadronElectoral getPadronElectoral(String documentoIdentidad);
}
