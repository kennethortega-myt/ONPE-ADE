package pe.gob.onpe.scebackend.model.enums;

public enum EstadoOficioEnum {
    PENDIENTE("P"), ENVIADA_JEE("I"), EXPEDIENTE_GENERADO("E"), ATENDIDO("J"), RECHAZADO_JEE("W"), RECHAZADO_ONPE("Z");

    private final String codigo;

    EstadoOficioEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static EstadoOficioEnum fromCodigo(String codigo) {
        for (EstadoOficioEnum estado : values()) {
            if (estado.codigo.equals(codigo)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado oficio inválido: " + codigo);
    }
}
