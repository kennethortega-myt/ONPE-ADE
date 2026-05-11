package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleConfiguracionDocumentoElectoralBaseDTO {

    private Integer tipoDato;

    private Integer habilitado;

    private BigDecimal pixelTopX;

    private BigDecimal pixelTopY;

    private BigDecimal pixelBottomX;

    private BigDecimal pixelBottomY;

    private BigDecimal coordenadaRelativaTopX;

    private BigDecimal coordenadaRelativaTopY;

    private BigDecimal coordenadaRelativaBottomX;

    private BigDecimal coordenadaRelativaBottomY;

    private BigDecimal width;

    private BigDecimal height;
 
    private Integer activo;
    
    private String usuario;
}
