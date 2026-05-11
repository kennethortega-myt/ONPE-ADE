package pe.gob.onpe.scebackend.utils.enums;

public enum TipoReporteEnum {
    MESAS_POR_ESTADO_DE_MESA(1),
    MESAS_POR_ESTADO_DE_ACTA(2),
    MESAS_POR_ESTADO_DE_DIGITACION(3);



    private final Integer value;
    TipoReporteEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
