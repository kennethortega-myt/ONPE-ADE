package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class ColorDTO {

    private String color;
    private Integer id;

    public ColorDTO(String color, Integer id){
        this.color = color;
        this.id = id;
    }
}
