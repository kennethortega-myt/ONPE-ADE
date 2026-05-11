package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.DistritoElectoralEmpateDTO;
import pe.gob.onpe.scebackend.model.dto.request.*;
import pe.gob.onpe.scebackend.model.dto.response.ActualizarResolucionResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.DistritoElectoralResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;


public interface ICifraRepartidoraService {
    List<DistritoElectoralResponseDto> listDistritoElectoral(DistritoElectoralRequestDto filtro);
    GenericResponse consultaCifraRepartidora(String esquema, ConsultaCifraRepartidoraRequestDto filtro);
    GenericResponse consolidarVotosAgrupacion(String esquema, ConsolidarVotosAgrupacionRequestDto filtro);
    GenericResponse reparteCurules(String esquema, ReparteCurulesRequestDto filtro);
    List<DistritoElectoralEmpateDTO> obtenerVotosEmpate(String esquema, ConsultaCifraRepartidoraRequestDto filtro);
    ActualizarResolucionResponseDto actualizarResolucion(String esquema, ActualizarResolucionRequestDto filtro);
    byte[] reporteCifraRepartidora(String esquema, ConsultaCifraRepartidoraRequestDto filtro) throws JRException;
    
}
