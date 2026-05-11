package pe.gob.onpe.scebackend.model.exportar.orc.dto;

import java.util.List;

import lombok.Data;
import pe.gob.onpe.scebackend.model.dto.AmbitoElectoralDto;


@Data
public class ExportOrcDto {

	private List<AmbitoElectoralDto> 	ambitoElectoral;
	private List<AgrupacionPoliticaExportDto> agrupacionPolitica;
	private List<AgrupacionPoliticaFicticioExportDto> agrupacionPoliticaFicticia;
	private List<AgrupacionPoliticaRealExportDto> agrupacionPoliticaReal;
	private List<CabActaExportDto>			acta;
	private List<DetActaExportDto>			detActa;
	private List<DetActaPreferencialExportDto>			detActaPreferencial;
	private List<DetUbigeoEleccionAgrupacionPoliticaExportDto> ubigeoEleccionAgrupacionPolitica;
	private List<EleccionExportDto> eleccion;
	private List<LocalVotacionExportDto> localVotacion;
	private List<MesaExportDto> mesa;
	private List<ProcesoElectoralExportDto> 		procesoElectoral;
	private List<UbigeoExportDto>					ubigeo;
	private List<UbigeoEleccionDto>			ubigeoEleccion;
	private List<CentroComputoExportDto>			centroComputo;
	private List<TabJuradoElectoralEspecialExportDto> juradoElectoralEspecial;
	private List<OrcCatalogoExportDto> catalogo;
	private List<AdmDetCatalogoEstructuraExportDto> catalogoEstructura;
	private List<OrcDetCatalogoReferenciaExportDto> catalogoReferencia;
	private List<OrcDetConfigDocElectoralExportDto> detalleConfiguracionDocumentoElectoral;
	private List<OrcDetTipoEleccionDocElectoralExportDto> detalleTipoEleccionDocumentoElectoral;
	private List<OrcDocElectoralExportDto> documentoElectoral;
	private List<OrcSeccionExportDto> seccion;
	private List<FormatoExportDto> formatos;
	private List<CabActaFormatoExportDto> cabActasFormatos;
	private List<DetActaFormatoExportDto> detActasFormatos;
	private List<CandidatoExportDto> candidatos;
	private List<CandidatoRealExportDto> candidatosReales;
	private List<CandidatoFicticioExportDto> candidatosFicticios;
	private List<DistritoElectoralExportDto> distritoElectorales;
	private List<VersionExportDto> version;
	private List<PuestaCeroExportDto> puestaCero;
	private List<UsuarioExportDto> usuario;
	private List<MiembroMesaSorteadoExportDto> miembroMesaSorteado;
	private List<DetActaOpcionExportDto> detActaOpcion;
	private List<OpcionVotoExportDto> opcionesVoto;
	private List<CabParametroExportDto> cabParametro;
	private List<DetParametroExportDto> detParametro;
	private List<DetDistritoElectoralEleccionExportDto> detalleDistritoElectoralEleccion;
	private List<VersionModeloExportDto> versionModelos;
	
}
