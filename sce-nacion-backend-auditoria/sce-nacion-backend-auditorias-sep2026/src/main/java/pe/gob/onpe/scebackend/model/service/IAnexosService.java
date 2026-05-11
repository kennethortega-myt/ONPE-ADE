package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.funciones.AnexoListaActaObservadaDTO;
import pe.gob.onpe.scebackend.model.dto.funciones.AnexosGeneralDTO;
import pe.gob.onpe.scebackend.model.dto.request.AnexosRequestDto;

import java.io.IOException;
import java.util.List;

public interface IAnexosService {

    List<AnexoListaActaObservadaDTO> listaActaObservada(AnexosRequestDto request);

    AnexosGeneralDTO anexo1(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO votos(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO votosCifras(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO votosCifrasInicial(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO tablaActas(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO mesasNoinstaladas(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO maestraOrganizacionPolitica(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO maestroUbigeo(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO odpe(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO anexo2(AnexosRequestDto request) throws GenericException, IOException;

    AnexosGeneralDTO all(AnexosRequestDto request) throws GenericException, IOException;
}
