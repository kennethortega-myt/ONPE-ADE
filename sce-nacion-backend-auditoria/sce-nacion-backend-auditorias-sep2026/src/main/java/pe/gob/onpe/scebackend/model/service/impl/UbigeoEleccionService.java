package pe.gob.onpe.scebackend.model.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.LocalVotacionDto;
import pe.gob.onpe.scebackend.model.dto.UbigeoDto;
import pe.gob.onpe.scebackend.model.dto.UbigeoNacionDTO;
import pe.gob.onpe.scebackend.model.orc.entities.UbigeoEleccion;
import pe.gob.onpe.scebackend.model.orc.repository.UbigeoEleccionRepository;
import pe.gob.onpe.scebackend.model.service.IUbigeoEleccionService;

@RequiredArgsConstructor
@Service
public class UbigeoEleccionService implements IUbigeoEleccionService {

    private final UbigeoEleccionRepository ubigeoEleccionRepository;
	
	// para nacion
	@Override
	@Transactional("locationTransactionManager")
    public UbigeoNacionDTO ubigeos(Long idEleccion) {
        List<UbigeoEleccion> listUbigeos = this.ubigeoEleccionRepository.findByEleccionId(idEleccion);
        List<UbigeoDto> listDepartamentos = new ArrayList<>();
        List<UbigeoDto> listProvincias = new ArrayList<>();

        Map<String, List<UbigeoDto>> departamentos = listUbigeos.stream().filter(x -> Objects.nonNull(x.getUbigeo().getUbigeoPadre()))
                .map(ub -> {
                    UbigeoDto ubigeo = new UbigeoDto();
                    ubigeo.setCodigo(ub.getUbigeo().getUbigeoPadre().getUbigeoPadre().getCodigo());
                    ubigeo.setId(ub.getId());
                    ubigeo.setIdPadre(Objects.isNull(ub.getUbigeo().getUbigeoPadre().getUbigeoPadre().getUbigeoPadre()) ? null : ub.getUbigeo().getUbigeoPadre().getUbigeoPadre().getUbigeoPadre().getId());
                    ubigeo.setNombre(ub.getUbigeo().getUbigeoPadre().getUbigeoPadre().getNombre());
                    return ubigeo;
                }).collect(Collectors.groupingBy(UbigeoDto::getCodigo));
        for (Map.Entry<String, List<UbigeoDto>> entrada : departamentos.entrySet()) {
            List<UbigeoDto> otra = entrada.getValue();
            if(otra.stream().findFirst().isPresent()) {
                listDepartamentos.add( otra.stream().findFirst().orElse(new UbigeoDto()));
            }

        }

        Map<String, List<UbigeoDto>> provincias = listUbigeos.stream().filter(x -> Objects.nonNull(x.getUbigeo().getUbigeoPadre())).map(ub -> {
            UbigeoDto ubigeo = new UbigeoDto();
            ubigeo.setCodigo(ub.getUbigeo().getUbigeoPadre().getCodigo());
            ubigeo.setId(ub.getId());
            ubigeo.setIdPadre(Objects.isNull(ub.getUbigeo().getUbigeoPadre().getUbigeoPadre()) ? null : ub.getUbigeo().getUbigeoPadre().getUbigeoPadre().getId());
            ubigeo.setNombre(ub.getUbigeo().getUbigeoPadre().getNombre());
            return ubigeo;
        }).collect(Collectors.groupingBy(UbigeoDto::getCodigo));

        for (Map.Entry<String, List<UbigeoDto>> entrada : provincias.entrySet()) {
            List<UbigeoDto> otra = entrada.getValue();
            listProvincias.add(otra.stream().findFirst().orElse(new UbigeoDto()));
        }

        List<UbigeoDto> distritos = listUbigeos.stream().filter(x -> Objects.nonNull(x.getUbigeo().getUbigeoPadre())).map(ub -> {
            UbigeoDto ubigeo = new UbigeoDto();
            ubigeo.setCodigo(ub.getUbigeo().getCodigo());
            ubigeo.setId(ub.getId());
            ubigeo.setIdPadre(Objects.isNull(ub.getUbigeo().getUbigeoPadre()) ? null : ub.getUbigeo().getUbigeoPadre().getId());
            ubigeo.setNombre(ub.getUbigeo().getNombre());
            ubigeo.setLocalesVotacion(ub.getUbigeo().getLocalVotacions().stream().map(lo->{
                LocalVotacionDto local = new LocalVotacionDto();
                local.setId(lo.getId());
                local.setNombre(lo.getNombre());
                return local;
            }).toList());
            return ubigeo;
        }).toList();


        return new UbigeoNacionDTO(listDepartamentos.stream().sorted((o1, o2) -> o1.getCodigo().
                        compareTo(o2.getCodigo())).toList(), listProvincias.stream().sorted((o1, o2) -> o1.getCodigo().
                        compareTo(o2.getCodigo())).toList(), distritos.stream().sorted((o1, o2) -> o1.getCodigo().
                        compareTo(o2.getCodigo())).toList());
    }
	
	
}
