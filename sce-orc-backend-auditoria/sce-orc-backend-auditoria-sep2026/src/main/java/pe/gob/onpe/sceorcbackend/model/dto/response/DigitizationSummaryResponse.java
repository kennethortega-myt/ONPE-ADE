package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitizationSummaryResponse {
    private Integer pending = 0;
    private Integer approved = 0;
    private Integer rejected = 0;
    private Integer digitalizados = 0;
    private Integer noInstaladosExtSin = 0;
}
