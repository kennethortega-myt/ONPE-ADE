package pe.gob.onpe.scebackend.model.dto.response;
import lombok.Data;
import java.io.Serializable;

@Data
public class MonitoreoGetFilesResponse implements Serializable {

    private String acta1File;
    private String acta2File;

    public MonitoreoGetFilesResponse() {
    }

    public MonitoreoGetFilesResponse(String acta1File, String acta2File) {
        this.acta1File = acta1File;
        this.acta2File = acta2File;
    }
}
