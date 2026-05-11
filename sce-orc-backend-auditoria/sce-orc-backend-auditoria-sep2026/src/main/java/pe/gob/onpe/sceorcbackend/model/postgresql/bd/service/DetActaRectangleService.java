package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaRectangle;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ActaIdSeccionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO;

import java.util.List;

public interface DetActaRectangleService extends CrudService<DetActaRectangle>{

    //List<DetActaRectangle> findByActaIdAndSeccion(Long idActa, String abreviaturaSeccion);

    List<DetActaRectangleDTO> findByActaId(Long idActa);

    List<ActaIdSeccionDTO> findActaIdAndEleccionIdWithRecordCount(List<Long> actaIds);

    //List<DetActaRectangle> findByActaIdAndSeccionIn(Long idActa, List<Integer> idSeccions);
    void deleteDetActaRectangleByActaId(Long idActa);

    void deleteInBatch();

    List<DetActaRectangleDTO> findByActaIdAndSeccion(Long idActa, Integer idSeccion);
}
