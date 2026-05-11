package pe.gob.onpe.scebackend.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VariableSistemaDto {
    String codigoVarible;
    String valorVariable;
}
