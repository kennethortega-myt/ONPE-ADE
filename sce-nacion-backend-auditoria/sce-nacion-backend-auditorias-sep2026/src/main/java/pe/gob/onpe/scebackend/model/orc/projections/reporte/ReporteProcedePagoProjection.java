package pe.gob.onpe.scebackend.model.orc.projections.reporte;

public interface ReporteProcedePagoProjection {
    Integer getNro();
    String getNumeroMesa();
    String getNumeroDocumento();
    String getVotante();
    String getCargo();
    String getProcedePago();
}