package pe.gob.onpe.sceorcbackend.utils;

import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetOtroDocumento;

import java.util.List;

public class DenunciaUtils {

    private DenunciaUtils() {

    }

    public static boolean validarDenunciaPerdidaParcial(String nroMesa, List<DetOtroDocumento> detOtroDocumentoList) {
        //valida las denuncias asociadas
        if (!detOtroDocumentoList.isEmpty() && detOtroDocumentoList.getFirst().getCabOtroDocumento().getEstadoDocumento().equals(ConstantesOtrosDocumentos.ESTADO_DOC_PROCESADO)){
            DetOtroDocumento detOtroDocumento = detOtroDocumentoList.getFirst();
            if(detOtroDocumento.getCodTipoPerdida().equals(ConstantesOtrosDocumentos.TIPO_PERDIDA_TOTAL)){
                throw new BadRequestException(
                        String.format("La lista de electores de la mesa %s, tiene asociada la denuncia %s con pérdida total.", nroMesa, detOtroDocumento.getCabOtroDocumento().getNumeroDocumento())
                );
            }else if(detOtroDocumento.getCodTipoPerdida().equals(ConstantesOtrosDocumentos.TIPO_PERDIDA_PARCIAL)){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
