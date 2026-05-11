package pe.gob.onpe.scebackend.model.stae.wrapper;


import lombok.AllArgsConstructor;
import lombok.Data;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaDto;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaPreferencialDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ErroresActaWrapper {

    private List<DetalleActaDto> detActaListToErrores;
    private List<DetalleActaPreferencialDto> detActaPreferencialListToErrores;
}
