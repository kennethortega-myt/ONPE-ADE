package pe.gob.onpe.sceorcbackend.model.dto.mapper;

import pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion.TabResolucionDTO;

public class UtilMapper {

    private UtilMapper(){}


    public static TabResolucionDTO tabResolucionToDto(pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion tabResolucion){
        if (tabResolucion == null){
            return null;
        }

        TabResolucionDTO dto = new TabResolucionDTO();
        dto.setId(tabResolucion.getId());
        dto.setArchivoResolucion(tabResolucion.getArchivoResolucion());
        dto.setArchivoResolucionPdf(tabResolucion.getArchivoResolucionPdf());
        dto.setProcedencia(tabResolucion.getProcedencia());
        dto.setFechaResolucion(tabResolucion.getFechaResolucion());
        dto.setNumeroExpediente(tabResolucion.getNumeroExpediente());
        dto.setNumeroResolucion(tabResolucion.getNumeroResolucion());
        dto.setTipoResolucion(tabResolucion.getTipoResolucion());
        dto.setEstadoResolucion(tabResolucion.getEstadoResolucion());
        dto.setEstadoDigitalizacion(tabResolucion.getEstadoDigitalizacion());
        dto.setObservacionDigitalizacion(tabResolucion.getObservacionDigitalizacion());
        dto.setNumeroPaginas(tabResolucion.getNumeroPaginas());
        dto.setActivo(tabResolucion.getActivo());
        dto.setAudUsuarioCreacion(tabResolucion.getAudUsuarioCreacion());
        dto.setAudFechaCreacion(tabResolucion.getAudFechaCreacion());
        dto.setAudUsuarioModificacion(tabResolucion.getAudUsuarioModificacion());
        dto.setAudFechaModificacion(tabResolucion.getAudFechaModificacion());
        dto.setAsignado(tabResolucion.getAsignado());
        dto.setAudUsuarioAsignado(tabResolucion.getAudUsuarioAsignado());
        dto.setAudFechaAsignado(tabResolucion.getAudFechaAsignado());
        return dto;
    }
    
}