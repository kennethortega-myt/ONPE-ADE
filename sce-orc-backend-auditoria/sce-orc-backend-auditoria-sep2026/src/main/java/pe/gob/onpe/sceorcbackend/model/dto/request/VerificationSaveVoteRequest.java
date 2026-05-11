package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;


@Data
public class VerificationSaveVoteRequest {
    private String token;

    private Integer cantidadVotosPreferenciales;

    private VerificationVoteItem[] items;

    private Long status;
}
