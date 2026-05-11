package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.dto.response.VerificationSignItem;

@Data
public class ArchivoEscrutinioFirma {

    private Long idActa;
    private VerificationSignItem countPresident;
    private VerificationSignItem countSecretary;
    private VerificationSignItem countThirdMember;


    private VerificationSignItem installPresident;
    private VerificationSignItem installSecretary;
    private VerificationSignItem installThirdMember;

    private VerificationSignItem votePresident;
    private VerificationSignItem voteSecretary;
    private VerificationSignItem voteThirdMember;

}
