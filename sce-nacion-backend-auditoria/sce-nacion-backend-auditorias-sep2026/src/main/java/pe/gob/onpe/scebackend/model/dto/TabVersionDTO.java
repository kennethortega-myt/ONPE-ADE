package pe.gob.onpe.scebackend.model.dto;

import java.sql.Timestamp;

public class TabVersionDTO {
    private String cadena;
    private Timestamp fechaCreacion;


    public String getCadena() { return cadena; }
    public void setCadena(String cadena) { this.cadena = cadena; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
