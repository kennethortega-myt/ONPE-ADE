package pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura;

public interface OrcDetalleCatalogoEstructuraProjection {
    Integer getId();
    String getColumna();
    String getNombre();
    Integer getCodigoI();
    String getCodigoS();
    String getTipo();
    Integer getOrden();
    Integer getActivo();

    Integer getCatalogo();
}
