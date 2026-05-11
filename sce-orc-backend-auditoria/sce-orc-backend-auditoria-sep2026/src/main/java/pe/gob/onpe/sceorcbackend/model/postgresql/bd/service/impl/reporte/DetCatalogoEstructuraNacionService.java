package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetCatalogoEstructuraRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetCatalogoEstructuraResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.IDetCatalogoEstructuraRepositoryCustom;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IDetCatalogoEstructuraNacionService;


import java.util.List;

@Service
public class DetCatalogoEstructuraNacionService implements IDetCatalogoEstructuraNacionService {

    @Autowired
    IDetCatalogoEstructuraRepositoryCustom detCatalogoEstructuraRepositoryCustom;

    @Override
    public List<DetCatalogoEstructuraResponseDto> listarDetCatalogoEstructura(DetCatalogoEstructuraRequestDto filtro) {
        return detCatalogoEstructuraRepositoryCustom.listarDetalleEstructura(filtro);
    }
}
