package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.Seccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.SeccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaRectangle;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaRectangleRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.EleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaRectangleService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ActaIdSeccionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ConstantesSecciones;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.util.*;

@Service
public class DetActaRectangleServiceImpl implements DetActaRectangleService {

    private final DetActaRectangleRepository detActaRectangleRepository;

    private final SeccionService adminSeccionService;

    private final EleccionRepository eleccionRepository;

    public DetActaRectangleServiceImpl(DetActaRectangleRepository detActaRectangleRepository,
                                       EleccionRepository eleccionRepository,
                                       SeccionService adminSeccionService) {
        this.detActaRectangleRepository = detActaRectangleRepository;
        this.eleccionRepository = eleccionRepository;
        this.adminSeccionService = adminSeccionService;
    }

    @Override
    public void save(DetActaRectangle detActaRectangle) {
        this.detActaRectangleRepository.save(detActaRectangle);
    }

    @Override
    public void saveAll(List<DetActaRectangle> k) {
        this.detActaRectangleRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detActaRectangleRepository.deleteAll();
    }

    @Override
    public List<DetActaRectangle> findAll() {
        return this.detActaRectangleRepository.findAll();
    }




    @Override
    public List<DetActaRectangleDTO> findByActaId(Long idActa) {
        return this.detActaRectangleRepository.findAllWithAbreviatura(idActa);
    }

    @Override
    public List<ActaIdSeccionDTO> findActaIdAndEleccionIdWithRecordCount(List<Long> actaIds) {

        List<Integer> eleccionGroup1 = this.eleccionRepository.findIdsByCodigos(List.of(
                ConstantesComunes.COD_ELEC_PRE,
                ConstantesComunes.COD_ELEC_REV_DIST ,
                ConstantesComunes.COD_ELEC_DIST));
        
        List<Integer> eleccionGroup2 = this.eleccionRepository.findIdsByCodigos(
                List.of(ConstantesComunes.COD_ELEC_PAR,
                        ConstantesComunes.COD_ELEC_DIPUTADO,
                        ConstantesComunes.COD_ELEC_SENADO_MULTIPLE,
                        ConstantesComunes.COD_ELEC_SENADO_UNICO));
        
        
        List<Seccion> adminSeccionList = getSeccionesVerificacion();
        List<Integer> idAbreviaturasNormal = getIdAbreviaturasNormal(adminSeccionList);
        List<Integer> idAbreviaturasPreferencial = getIdAbreviaturasPreferenciasl(adminSeccionList);

        Long cantidadSeccionesNormal = (long)idAbreviaturasNormal.size() + 1 ;
        Long cantidadSeccionesPreferencial = (long)idAbreviaturasPreferencial.size() + 1;

        return this.detActaRectangleRepository.findActaIdAndEleccionIdWithRecordCount(actaIds,
                idAbreviaturasPreferencial, cantidadSeccionesNormal,
                cantidadSeccionesPreferencial,
                eleccionGroup1, eleccionGroup2);
    }


    public List<Integer> getIdAbreviaturasNormal(List<Seccion> adminSeccionList){
        return adminSeccionList.stream()
                .filter(seccion -> !seccion.getAbreviatura().equals(ConstantesSecciones.SECTION_ABREV_VOTE_PREFERENCIAL))
                .map(Seccion::getId)
                .toList();
    }

    public List<Integer> getIdAbreviaturasPreferenciasl(List<Seccion> adminSeccionList){
        return adminSeccionList.stream()
                .map(Seccion::getId)
                .toList();
    }

    
    public List<Seccion> getSeccionesVerificacion(){
        //Todas las secciones que se necesitan, saber su ID
       return this.adminSeccionService.findIdsByAbreviaturas(List.of(
               //ConstantesSecciones.SECTION_ABREV_CODIGO_BARRAS,
               ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_PRESIDENT,
               ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_SECRETARY,
               ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_THIRD_MEMBER,
               ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_PRESIDENT,
               ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_SECRETARY,
               ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_THIRD_MEMBER,
               ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_PRESIDENT,
               ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_SECRETARY,
               ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_THIRD_MEMBER,
               ConstantesSecciones.SECTION_ABREV_VOTE,
               ConstantesSecciones.SECTION_ABREV_VOTE_PREFERENCIAL,
               ConstantesSecciones.SECTION_ABREV_OBSERVATION_COUNT,
               ConstantesSecciones.SECTION_ABREV_OBSERVATION_VOTE,
               ConstantesSecciones.SECTION_ABREV_OBSERVATION_INSTALL,
               ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_LETTERS,
               ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_NUMBERS,
               ConstantesSecciones.SECTION_ABREV_START_TIME,
               ConstantesSecciones.SECTION_ABREV_FINISH_TIME)
       );
    }



    @Override
    public void deleteDetActaRectangleByActaId(Long idActa) {
        this.detActaRectangleRepository.deleteDetActaRectangleByActaId(idActa);
    }

    @Override
    public void deleteInBatch() {
        this.detActaRectangleRepository.deleteAllInBatch();
    }

    @Override
    public List<DetActaRectangleDTO> findByActaIdAndSeccion(Long idActa, Integer idSeccion) {
        return this.detActaRectangleRepository.findByActaAndSeccion(idActa, idSeccion);
    }

}
