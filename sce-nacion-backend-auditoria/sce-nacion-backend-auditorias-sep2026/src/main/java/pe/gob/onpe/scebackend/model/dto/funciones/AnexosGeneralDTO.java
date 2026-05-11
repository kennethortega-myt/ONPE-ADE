package pe.gob.onpe.scebackend.model.dto.funciones;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class AnexosGeneralDTO {

    private byte[] byteFile;
    private List<File> files;
    private File file;


}
