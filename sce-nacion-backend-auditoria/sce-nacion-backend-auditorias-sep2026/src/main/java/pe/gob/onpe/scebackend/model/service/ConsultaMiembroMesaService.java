package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;

public interface ConsultaMiembroMesaService {

    GenericResponse consultarMiembrosMesa(Integer numeroMesa, String dni);
     GenericResponse getUbigeoByAcronimo(String acronimo);
}
