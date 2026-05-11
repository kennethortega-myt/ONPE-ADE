package pe.gob.onpe.sceorcbackend.model.dto.util;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class HashModelo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private String sha256;

}
