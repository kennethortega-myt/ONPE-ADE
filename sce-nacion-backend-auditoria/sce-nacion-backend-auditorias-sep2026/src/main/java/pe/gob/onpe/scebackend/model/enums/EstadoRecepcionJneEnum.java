package pe.gob.onpe.scebackend.model.enums;

public enum EstadoRecepcionJneEnum {
    PENDIENTE((short) 0), ENVIADO((short) 1), ERROR((short) 2), EN_PROCESO((short) 3);

    private final short codigo;

    EstadoRecepcionJneEnum(short codigo) {
        this.codigo = codigo;
    }

    public short getCodigo() {
        return codigo;
    }
}
