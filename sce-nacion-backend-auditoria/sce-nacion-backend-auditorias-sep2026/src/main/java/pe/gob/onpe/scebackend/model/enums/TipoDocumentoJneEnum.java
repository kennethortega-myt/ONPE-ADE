package pe.gob.onpe.scebackend.model.enums;

public enum TipoDocumentoJneEnum {
    ERROR("01"), EXPEDIENTE("02"), RESOLUCION("03");

    private final String codigo;

    TipoDocumentoJneEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static TipoDocumentoJneEnum fromCodigo(String codigo) {
        for (TipoDocumentoJneEnum t : values()) {
            if (t.codigo.equals(codigo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo documento inválido: " + codigo);
    }
}
