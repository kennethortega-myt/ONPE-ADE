package pe.gob.onpe.scebackend.model.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.DetCatalogoEstructuraDto;
import pe.gob.onpe.scebackend.model.orc.entities.OrcCatalogo;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.scebackend.model.orc.projections.DetalleCatalogoEstructuraProjection;
import pe.gob.onpe.scebackend.model.orc.repository.OrcCatalogoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.OrcDetalleCatalogoEstructuraRepository;
import pe.gob.onpe.scebackend.model.service.IDetalleCatalogoEstructuraService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class DetalleCatalogoEstructuraServiceImpl implements IDetalleCatalogoEstructuraService {


    private final OrcDetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository;
    private final OrcCatalogoRepository catalogoRepository;
    Logger logger = LoggerFactory.getLogger(DetalleCatalogoEstructuraServiceImpl.class);

    public DetalleCatalogoEstructuraServiceImpl(
            OrcDetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository,
            OrcCatalogoRepository catalogoRepository) {
        this.detalleCatalogoEstructuraRepository = detalleCatalogoEstructuraRepository;
        this.catalogoRepository = catalogoRepository;
    }
    
    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public List<DetCatalogoEstructuraDto> findByMaestroAndColumna(String cMaestro, String cColumna){
        List<OrcCatalogo> catalogos = this.catalogoRepository.findByMaestro(cMaestro);
        if(!catalogos.isEmpty()){
            OrcCatalogo catalogo = catalogos.get(0);
            List<DetalleCatalogoEstructuraProjection> projections =
                    this.detalleCatalogoEstructuraRepository.findByCatalogoId(catalogo.getId(), cColumna, ConstantesComunes.ACTIVO);
            return projections.stream().map(projection -> new DetCatalogoEstructuraDto(
                    projection.getColumna(),
                    projection.getNombre(),
                    projection.getCodigoI(),
                    projection.getCodigoS(),
                    projection.getOrden(),
                    projection.getActivo()
            )).toList();
        }else{
            return new ArrayList<>();
        }

    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public void save(OrcDetalleCatalogoEstructura orcDetalleCatalogoEstructura) {
        this.detalleCatalogoEstructuraRepository.save(orcDetalleCatalogoEstructura);
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public void saveAll(List<OrcDetalleCatalogoEstructura> k) {
        this.detalleCatalogoEstructuraRepository.saveAll(k);
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public void deleteAll() {
        this.detalleCatalogoEstructuraRepository.deleteAll();
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public List<OrcDetalleCatalogoEstructura> findAll() {
        return List.of();
    }
}
