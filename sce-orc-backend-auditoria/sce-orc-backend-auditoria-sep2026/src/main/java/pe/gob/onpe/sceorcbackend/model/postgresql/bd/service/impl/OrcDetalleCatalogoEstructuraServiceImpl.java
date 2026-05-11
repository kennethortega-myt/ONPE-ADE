package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcCatalogo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CatalogoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetalleCatalogoEstructuraRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.OrcDetalleCatalogoEstructuraProjection;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrcDetalleCatalogoEstructuraServiceImpl implements OrcDetalleCatalogoEstructuraService {

    private final CatalogoRepository catalogoRepository;
    private final DetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository;

    public OrcDetalleCatalogoEstructuraServiceImpl(CatalogoRepository catalogoRepository,
                                                   DetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository) {
        this.catalogoRepository = catalogoRepository;
        this.detalleCatalogoEstructuraRepository = detalleCatalogoEstructuraRepository;
    }

    @Override
    public void save(OrcDetalleCatalogoEstructura orcDetalleCatalogoEstructura) {
        this.detalleCatalogoEstructuraRepository.save(orcDetalleCatalogoEstructura);
    }

    @Override
    public void saveAll(List<OrcDetalleCatalogoEstructura> k) {
        this.detalleCatalogoEstructuraRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detalleCatalogoEstructuraRepository.deleteAll();
    }

    @Override
    public List<OrcDetalleCatalogoEstructura> findAll() {
        return this.detalleCatalogoEstructuraRepository.findAll();
    }

    @Override
    public List<DetCatalogoEstructuraDTO> findByMaestroAndColumna(String maestro, String columna) {
        List<OrcCatalogo> orcCatalogos = this.catalogoRepository.findByMaestro(maestro);
        if (!orcCatalogos.isEmpty()){
            OrcCatalogo orcCatalogo = orcCatalogos.get(0);
            List<OrcDetalleCatalogoEstructuraProjection> proyecciones = this.detalleCatalogoEstructuraRepository.findByCatalogo(orcCatalogo.getId(),columna, ConstantesComunes.ACTIVO);
            List<DetCatalogoEstructuraDTO> resultado = proyecciones.stream().map(projection -> new DetCatalogoEstructuraDTO(
                    projection.getId(),
                    projection.getColumna(),
                    projection.getNombre(),
                    projection.getCodigoI(),
                    projection.getCodigoS(),
                    projection.getTipo(),
                    projection.getOrden(),
                    projection.getActivo(),
                    projection.getCatalogo()
            )).toList();
            return resultado;
        }else{
            return new ArrayList<>();
        }
    }
}
