package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportarDto {

    private String idCentroComputo;
    private String proceso;
    private List<AmbitoElectoralDto> ambitoElectoral;
    private List<AgrupacionPoliticaDto> agrupacionPolitica;
    private List<AgrupacionPoliticaFicticioDto> agrupacionPoliticaFicticia;
    private List<AgrupacionPoliticaRealDto> agrupacionPoliticaReal;
    private List<ArchivoDto> archivo;
    private List<CabActaDto> acta;
    private List<DetActaDto> detActa;
    private List<DetActaPreferencialDto> detActaPreferencial;
    private List<DetUbigeoEleccionAgrupacionPoliticaDto> ubigeoEleccionAgrupacionPolitica;
    private List<EleccionDto> eleccion;
    private List<LocalVotacionDto> localVotacion;
    private List<MesaDto> mesa;
    private List<ProcesoElectoralDto> procesoElectoral;
    private List<UbigeoDto> ubigeo;
    private List<UbigeoEleccionDto> ubigeoEleccion;
    private List<CentroComputoDto> centroComputo;
    private List<CatalogoDto> catalogo;
    private List<DetCatalogoEstructuraDto> catalogoEstructura;
    private List<DetCatalogoReferenciaDto> catalogoReferencia;
    private List<FormatoDto> formatos;
    private List<CabActaFormatoDto> cabActasFormatos;
    private List<DetActaFormatoDto> detActasFormatos;
    private List<CandidatoDto> candidatos;
    private List<CandidatoFicticioDto> candidatosFicticios;
    private List<CandidatoRealDto> candidatosReales;
    private List<DistritoElectoralDto> distritoElectorales;
    private List<VersionDto> version;
    private List<VersionModeloDto> versionModelos;
    private List<PuestaCeroDto> puestaCero;
    private List<UsuarioDto> usuario;
    private List<MiembroMesaSorteadoDto> miembroMesaSorteado;
    private List<DetActaOpcionDto> detActaOpcion;
	private List<OpcionVotoDto> opcionesVoto;
	private List<CabParametroDto> cabParametro;
	private List<DetParametroDto> detParametro;
	private List<DetDistritoElectoralEleccionDto> detalleDistritoElectoralEleccion;
	private List<TabJuradoElectoralEspecialDto> juradoElectoralEspecial;

    private List<AdmDetConfigDocElectoralHistDto> detalleConfiguracionDocumentoElectoral;
    private List<AdmDetTipoEleccionDocElectoralHistDto> detalleTipoEleccionDocumentoElectoral;
    private List<AdmDocElectoralDto> documentoElectoral;
    private List<AdmSeccionDto> seccion;
    

}
