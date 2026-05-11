package pe.gob.onpe.scebackend.model.dto.reportes;

import net.sf.jasperreports.engine.JasperPrint;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;

import java.util.List;

public record BaseReporteParams(
        Long idPuestaCero,
        ProcesoElectoral proceso,
        String codigoCcDescripcion,
        String codAmbitoDescripcion,
        String usuario,
        List<JasperPrint> prints
) {}