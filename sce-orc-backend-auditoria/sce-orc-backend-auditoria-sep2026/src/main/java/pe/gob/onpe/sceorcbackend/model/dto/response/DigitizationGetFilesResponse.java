package pe.gob.onpe.sceorcbackend.model.dto.response;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitizationGetFilesResponse implements Serializable {

    private static final long serialVersionUID = 8573174203328059993L;

    private String acta1File;
    private String acta2File;
    private String acta3File;

    public DigitizationGetFilesResponse() {
    }

    public DigitizationGetFilesResponse(String acta1File, String acta2File, String acta3File) {
        this.acta1File = acta1File;
        this.acta2File = acta2File;
        this.acta3File = acta3File;
    }
}
