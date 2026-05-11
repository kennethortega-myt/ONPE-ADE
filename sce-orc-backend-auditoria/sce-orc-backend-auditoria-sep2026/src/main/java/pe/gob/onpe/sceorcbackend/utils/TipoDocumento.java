package pe.gob.onpe.sceorcbackend.utils;

import java.util.Objects;

public enum TipoDocumento {
    MESA_ESCRUTINIO("ME", "Miembros de Mesa según Acta de Escrutinio (MMAE)"),
    PERSONEROS("PR", "Personeros (PER)"),
    LISTA_ELECTORES("LE", "Lista de electores (LE)"),
    HOJA_ASISTENCIA("MM", "Hoja de asistencia (HA)");

    private final String id;
    private final String descripcion;

    TipoDocumento(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoDocumento fromId(String id) {
        for (TipoDocumento estado : values()) {
            if (Objects.equals(estado.id, id)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("ID no válido: " + id);
    }
}
