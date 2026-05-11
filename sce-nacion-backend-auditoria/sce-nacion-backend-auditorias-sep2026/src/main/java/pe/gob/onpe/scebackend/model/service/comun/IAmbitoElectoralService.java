package pe.gob.onpe.scebackend.model.service.comun;


import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;
import pe.gob.onpe.scebackend.model.service.CrudService;

public interface IAmbitoElectoralService extends CrudService<AmbitoElectoral> {

    AmbitoElectoral getById(Long id);

    AmbitoElectoral getPadreNacion();
}
