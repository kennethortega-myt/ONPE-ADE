package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.orc.entities.TransmisionEnvio;

import java.util.List;


public interface ITransmisionEnvioService {

    List<TransmisionEnvio> listbyIdActa(Long idActa);

}
