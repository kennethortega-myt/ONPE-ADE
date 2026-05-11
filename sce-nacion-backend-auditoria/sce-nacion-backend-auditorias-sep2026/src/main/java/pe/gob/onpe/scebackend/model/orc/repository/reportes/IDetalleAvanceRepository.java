package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroDetalleAvanceDto;
import pe.gob.onpe.scebackend.model.dto.reportes.DetalleAvanceMiembrosMesaEscrutinioDto;
import pe.gob.onpe.scebackend.model.dto.reportes.DetalleAvancePersonerosDto;

import java.sql.SQLException;
import java.util.List;

public interface IDetalleAvanceRepository {
    List<DetalleAvanceMiembrosMesaEscrutinioDto> listaDetalleAvanceMiembrosMesaEscrutinio(FiltroDetalleAvanceDto filtro) throws SQLException;
    List<DetalleAvancePersonerosDto> listaDetalleAvancePersoneros(FiltroDetalleAvanceDto filtro) throws SQLException;
}
