package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import pe.gob.onpe.scebackend.model.dto.reportes.ComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroComparacionDigitacionAutomaticaDto;

import java.util.List;

public interface IComparacionDigitacionAutomaticaRepository {
    List<ComparacionDigitacionAutomaticaDto> listaComparacionDigitacionAutomatica(FiltroComparacionDigitacionAutomaticaDto filtro);
    List<ComparacionDigitacionAutomaticaDto> listaComparacionDigitacionAutomaticaPref(FiltroComparacionDigitacionAutomaticaDto filtro);
}
