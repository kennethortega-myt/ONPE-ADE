package pe.gob.onpe.scebackend.utils.enums;

public enum TipoAgrupadoEnum {
    POR_LOCAL_VOTACION(1),
    POR_CENTRO_COMPUTO(2);
    private final Integer value;
    TipoAgrupadoEnum(Integer value) {
        this.value = value;
    }
    public Integer getValue() {
        return value;
    }
}
