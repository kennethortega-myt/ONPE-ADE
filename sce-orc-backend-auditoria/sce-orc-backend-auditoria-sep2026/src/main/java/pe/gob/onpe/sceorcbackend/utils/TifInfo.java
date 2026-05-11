package pe.gob.onpe.sceorcbackend.utils;

import lombok.Data;

@Data
public class TifInfo {
    private final String nombre;
    private final int pagina;
    private final int paginaMaxima;

    public TifInfo(String nombre, int pagina, int paginaMaxima) {
        this.nombre = nombre;
        this.pagina = pagina;
        this.paginaMaxima = paginaMaxima;

    }

}
