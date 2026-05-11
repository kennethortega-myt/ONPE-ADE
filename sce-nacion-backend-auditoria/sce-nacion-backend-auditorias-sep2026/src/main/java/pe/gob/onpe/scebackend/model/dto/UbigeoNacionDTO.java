package pe.gob.onpe.scebackend.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UbigeoNacionDTO {

    private List<UbigeoDto> departamentos;
    private List<UbigeoDto> provincias;
    private List<UbigeoDto> distritos;
}
