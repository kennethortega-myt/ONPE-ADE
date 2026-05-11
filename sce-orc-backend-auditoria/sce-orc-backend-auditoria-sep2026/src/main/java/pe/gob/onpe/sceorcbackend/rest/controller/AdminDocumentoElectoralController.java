package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.AdminDocumentoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.ListarAdminDocumentoElectoralResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.PropiedadDocumentoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@PreAuthorize(RoleAutority.ACCESO_TOTAL)
@RestController
@RequestMapping("/admin-documento-electoral")
public class AdminDocumentoElectoralController {

    private final DocumentoElectoralService documentoElectoralService;
    private final TokenUtilService tokenUtilService;

    public AdminDocumentoElectoralController(DocumentoElectoralService documentoElectoralService,
                                             TokenUtilService tokenUtilService) {
        this.documentoElectoralService = documentoElectoralService;
        this.tokenUtilService = tokenUtilService;
    }


    /**
     * Uso para el sce-scanner LISTA DESPLEGABLE PARA DIGITALIZAR
     * */
    @GetMapping("")
    public ResponseEntity<ListarAdminDocumentoElectoralResponse> getAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);
        List<DocumentoElectoral> docs = documentoElectoralService.buscarDocumentosConfigurados(tokenInfo.getAbrevProceso());

        docs.removeIf(doc -> ConstantesComunes.ABREV_ACTA_ESCRUTINIO.equalsIgnoreCase(doc.getAbreviatura()));
        docs.removeIf(doc -> ConstantesComunes.SOLUCION_TECNOLOGICA_TEXT_STAE.equalsIgnoreCase(doc.getAbreviatura()));
        docs.removeIf(doc -> ConstantesComunes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL.equalsIgnoreCase(doc.getAbreviatura()));
        docs.removeIf(doc -> ConstantesComunes.ABREV_ACTA_INSTALACION_SUGRAFIO.equalsIgnoreCase(doc.getAbreviatura()));

        List<AdminDocumentoElectoralDto> resultado = new ArrayList<>();

        for (DocumentoElectoral padre : docs) {
            AdminDocumentoElectoralDto dto = new AdminDocumentoElectoralDto();
            dto.setNombreDoc(padre.getNombre());
            dto.setVisible(padre.getVisible());
            dto.setDescCorta(padre.getAbreviatura());
            resultado.add(dto);
        }

        return ResponseEntity.ok(new ListarAdminDocumentoElectoralResponse(true,"Se obtuvo correctamente la lista de documentos electorales.", resultado));
    }

    /**
     * Uso para el sce-scanner MENUVBOX - VER DOCUMENTOS
     * */
    @GetMapping("/lista-simple")
    public ResponseEntity<GenericResponse<List<PropiedadDocumentoElectoralDto>>> getAllSimple(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);
        List<DocumentoElectoral> docs = documentoElectoralService.buscarDocumentosConfigurados(tokenInfo.getAbrevProceso());

        List<DocumentoElectoral> ordenados = docs.stream()
            .sorted(Comparator.comparing(DocumentoElectoral::getNombre))
            .toList();

        List<Long> padres = ordenados.stream()
            .filter(doc -> doc.getDocumentoElectoralPadre() != null)
            .map(doc -> doc.getDocumentoElectoralPadre().getId().longValue())
            .toList();

        //remuevo a los padres
        List<PropiedadDocumentoElectoralDto> resultado = new ArrayList<>(ordenados.stream()
            .filter(doc -> !padres.contains(doc.getId().longValue()))
            .map(this::mapToPropiedadDto)
            .toList());


        actualizarListaSimple(resultado);

        return ResponseEntity.ok(new GenericResponse<>(true,"Se obtuvo correctamente la lista simple de documentos electorales." ,resultado));
    }


    /**
     * Uso para el sce-scanner MENUVBOX - VER DOCUMENTOS
     * */
    @GetMapping("/lista-documentos-principales")
    public ResponseEntity<GenericResponse<List<PropiedadDocumentoElectoralDto>>> getListaDocumentosPadres() {

        List<DocumentoElectoral> docs = documentoElectoralService.findByDocumentoElectoralPadreIsNull();

        docs.removeIf(r -> ConstantesComunes.SOLUCION_TECNOLOGICA_TEXT_STAE.equalsIgnoreCase(r.getAbreviatura()));
        docs.removeIf(r -> ConstantesComunes.ABREV_ACTA_ESCRUTINIO_EXTRANJERO_PADRE.equalsIgnoreCase(r.getAbreviatura()));

        List<DocumentoElectoral> ordenados = docs.stream()
                .sorted(Comparator.comparing(DocumentoElectoral::getNombre))
                .toList();

        //remuevo a los padres
        List<PropiedadDocumentoElectoralDto> resultado = new ArrayList<>(ordenados.stream()
                .map(this::mapToPropiedadDto)
                .toList());

        return ResponseEntity.ok(new GenericResponse<>(true,"Se obtuvo correctamente la lista de documentos electorales padrez." ,resultado));

    }


    private void actualizarListaSimple(List<PropiedadDocumentoElectoralDto> resultado) {
        resultado.sort(Comparator.comparing(r -> r.getDescCorta().toUpperCase()));
    }


    private PropiedadDocumentoElectoralDto mapToPropiedadDto(DocumentoElectoral doc) {
        PropiedadDocumentoElectoralDto dto = new PropiedadDocumentoElectoralDto();
        dto.setId(doc.getId().longValue());
        dto.setNombreDoc(doc.getNombre());
        dto.setDescCorta(doc.getAbreviatura());
        dto.setEscanerAmbasCaras(doc.getEscanerAmbasCaras());
        dto.setMultipagina(doc.getMultipagina());
        dto.setTamanioHoja(doc.getTamanioHoja());
        dto.setTipoImagen(doc.getTipoImagen());
        dto.setCodBarOrientacion(doc.getCodigoBarraOrientacion());
        dto.setCodBarLeft(parseOrDefault(doc.getCodigoBarraPixelBottomX()));
        dto.setCodBarTop(parseOrDefault(doc.getCodigoBarraPixelBottomY()));
        dto.setCodBarWidth(parseOrDefault(doc.getCodigoBarraWidth()));
        dto.setCodBarHeight(parseOrDefault(doc.getCodigoBarraHeight()));
        dto.setActivo(doc.getActivo());
        dto.setVisible(doc.getVisible());
        return dto;
    }



    private Integer parseOrDefault(String value) {
        try{
            if(value.equals(ConstantesComunes.VACIO)) return null;
            return Integer.valueOf(value);
        }catch (Exception e){
            return null;
        }
    }

}
