package pe.gob.onpe.sceorcbackend.model.mapper;

import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.SeguimientoOficioDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DetTipoEleccionDocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOficio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Oficio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;

public final class SeguimientoOficioMapper {

    private SeguimientoOficioMapper() {
    }

    public static SeguimientoOficioDTO build(Oficio oficio, DetActaOficio detalle,
            DetTipoEleccionDocumentoElectoralService eleccionService) {

        SeguimientoOficioDTO dto = new SeguimientoOficioDTO();

        dto.setIdOficio(oficio.getId().longValue());
        dto.setNumeroficio(oficio.getNombreOficio());
        dto.setEstadoOficio(oficio.getEstado());
        dto.setFechaEnvio(oficio.getFechaEnvio());
        dto.setFechaRespuesta(oficio.getFechaRespuesta());

        completarActa(dto, detalle, eleccionService);
        completarCeleste(dto, detalle);
        completarResolucion(dto, detalle);

        return dto;
    }

    private static void completarActa(SeguimientoOficioDTO dto, DetActaOficio detalle,
            DetTipoEleccionDocumentoElectoralService eleccionService) {

        Acta acta = detalle.getActa();
        if (acta == null || acta.getMesa() == null)
            return;

        String mesa = acta.getMesa().getCodigo();

        dto.setActaPlomaId(acta.getId());
        dto.setNumeroActaPloma(
                mesa + "-" + acta.getNumeroCopia() + Optional.ofNullable(acta.getDigitoChequeoEscrutinio()).orElse(""));

        boolean esStaeIntegrada = Long.valueOf(2).equals(acta.getSolucionTecnologica())
                && Integer.valueOf(1).equals(acta.getTipoTransmision());
        dto.setStaeIntegrada(esStaeIntegrada);

        if (esStaeIntegrada) {
            if (acta.getArchivoEscrutinioFirmado() != null)
                dto.setIdArchivoEscrutinioFirmado(acta.getArchivoEscrutinioFirmado().getId().toString());

            if (acta.getArchivoInstalacionFirmado() != null)
                dto.setIdArchivoInstalacionFirmado(acta.getArchivoInstalacionFirmado().getId().toString());

            if (acta.getArchivoSufragioFirmado() != null)
                dto.setIdArchivoSufragioFirmado(acta.getArchivoSufragioFirmado().getId().toString());

        } else {
            if (acta.getArchivoEscrutinio() != null)
                dto.setIdArchivoEscrutinio(acta.getArchivoEscrutinio().getId().toString());

            if (acta.getArchivoInstalacionSufragio() != null)
                dto.setIdArchivoInstalacionSufragio(acta.getArchivoInstalacionSufragio().getId().toString());
        }

        asignarEleccion(dto, acta, eleccionService);
    }

    private static void completarCeleste(SeguimientoOficioDTO dto, DetActaOficio detalle) {

        if (Boolean.TRUE.equals(dto.isStaeIntegrada())) {
            return;
        }

        ActaCeleste celeste = detalle.getActaCeleste();
        Acta acta = detalle.getActa();

        if (celeste == null)
            return;

        String mesa = (acta != null && acta.getMesa() != null) ? acta.getMesa().getCodigo() : "";

        dto.setActaCelesteId(celeste.getId());
        dto.setNumeroActaCeleste(mesa + "-" + celeste.getNumeroCopia()
                + Optional.ofNullable(celeste.getDigitoChequeoEscrutinio()).orElse(""));
    }

    private static void asignarEleccion(SeguimientoOficioDTO dto, Acta acta,
            DetTipoEleccionDocumentoElectoralService eleccionService) {

        DetTipoEleccionDocumentoElectoral eleccion = eleccionService.findByCopia(acta.getNumeroCopia());

        if (eleccion != null && eleccion.getEleccion() != null) {
            dto.setEleccion(eleccion.getEleccion().getNombre());
        }
    }

    private static void completarResolucion(SeguimientoOficioDTO dto, DetActaOficio detalle) {
        dto.setNumeroResolucion(Optional.ofNullable(detalle.getNumeroResolucionJNE()).orElse(""));
        dto.setNumeroExpediente(Optional.ofNullable(detalle.getNumeroExpedienteJNE()).orElse(""));
        dto.setArchivoJNE(
                Optional.ofNullable(detalle.getArchivoJNE())
                        .map(Archivo::getId)
                        .map(String::valueOf)
                        .orElse("")
            );

        TabResolucion resolucion = detalle.getResolucion();
        if (resolucion != null)
            dto.setIdResolucion(resolucion.getId());
    }
}
