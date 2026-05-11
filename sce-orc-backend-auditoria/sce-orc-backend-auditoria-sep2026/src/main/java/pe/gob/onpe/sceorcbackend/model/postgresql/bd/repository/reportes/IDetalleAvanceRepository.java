package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroDetalleAvanceDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleAvanceMiembrosMesaEscrutinioDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleAvancePersonerosDto;

import java.sql.SQLException;
import java.util.List;

public interface IDetalleAvanceRepository {
    List<DetalleAvanceMiembrosMesaEscrutinioDto> listaDetalleAvanceMiembrosMesaEscrutinio(FiltroDetalleAvanceDto filtro) throws SQLException;
    List<DetalleAvancePersonerosDto> listaDetalleAvancePersoneros(FiltroDetalleAvanceDto filtro) throws SQLException;
}
