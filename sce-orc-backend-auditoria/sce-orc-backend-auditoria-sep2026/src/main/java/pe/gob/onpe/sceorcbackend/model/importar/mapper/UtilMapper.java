package pe.gob.onpe.sceorcbackend.model.importar.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmDetConfigDocElectoralHistDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmDetTipoEleccionDocElectoralHistDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmDocElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmSeccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AgrupacionPoliticaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AgrupacionPoliticaFicticioDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AgrupacionPoliticaRealDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AmbitoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.ArchivoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CabActaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CabActaFormatoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CabParametroDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CandidatoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CandidatoFicticioDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CandidatoRealDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CatalogoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetActaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetActaFormatoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetActaOpcionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetActaPreferencialDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetCatalogoEstructuraDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetCatalogoReferenciaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetDistritoElectoralEleccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetParametroDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetUbigeoEleccionAgrupacionPoliticaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DistritoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.FormatoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.LocalVotacionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.MaePadronDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.MesaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.MiembroMesaSorteadoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.OpcionVotoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.ProcesoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.PuestaCeroDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.TabJuradoElectoralEspecialDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.UbigeoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.UbigeoEleccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.UsuarioDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.VersionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.VersionModeloDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAgrupacionPoliticaFicticia;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAgrupacionPoliticaReal;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAmbitoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportArchivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCabActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCabParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidato;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidatoFicticio;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidatoReal;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetActaPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetDistritoElectoralEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetalleConfiguracionDocumentoElectoralHistorial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetalleTipoEleccionDocumentoElectoralHistorial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDistritoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportLocalVotacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportMesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportMiembroMesaSorteado;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOpcionVoto;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOrcCatalogo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOrcDetalleCatalogoEstructura;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOrcDetalleCatalogoReferencia;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPadronElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPuestaCero;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportSeccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportTabJuradoElectoralEspecial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportUbigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportUbigeoEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportUsuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportVersion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportVersionModelo;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

public class UtilMapper {

	static Logger logger = LoggerFactory.getLogger(UtilMapper.class);
	
	private UtilMapper(){}
	
	public static ImportCentroComputo convertirCentroComputo(CentroComputoDto dto) {
		ImportCentroComputo entity = new ImportCentroComputo();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setCodigo(dto.getCodigo());
        entity.setApiTokenBackedCc(dto.getApiTokenBackedCc());
        entity.setIpBackendCc(dto.getIpBackendCc());
        entity.setPuertoBackedCc(dto.getPuertoBackedCc());
        entity.setActivo(dto.getActivo());
        
        if(dto.getIdPadre()!=null) {
        	ImportCentroComputo padre = new ImportCentroComputo();
        	padre.setId(dto.getIdPadre());
        	entity.setCentroComputoPadre(padre);       
        }
        
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportAmbitoElectoral convertirAmbitoElectoral(AmbitoElectoralDto dto) {
		ImportAmbitoElectoral entity = new ImportAmbitoElectoral();
		entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setTipoAmbitoElectoral(dto.getTipoAmbitoElectoral());
        
        if(dto.getIdPadre()!=null) {
        	ImportAmbitoElectoral padre = new ImportAmbitoElectoral();
        	padre.setId(dto.getIdPadre());        	
        	entity.setAmbitoElectoralPadre(padre);
        }
        
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportAgrupacionPolitica convertirAgrupacionPolitica(AgrupacionPoliticaDto dto) {
		ImportAgrupacionPolitica entity = new ImportAgrupacionPolitica();
        entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipoAgrupacionPolitica(dto.getTipoAgrupacionPolitica());
        entity.setEstado(dto.getEstado());
        entity.setActivo(dto.getActivo());       
        entity.setUbigeoMaximo(dto.getUbigeoMaximo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportAgrupacionPoliticaFicticia convertirAgrupacionPoliticaFicticio(AgrupacionPoliticaFicticioDto dto) {
		ImportAgrupacionPoliticaFicticia entity = new ImportAgrupacionPoliticaFicticia();
        entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipoAgrupacionPolitica(dto.getTipoAgrupacionPolitica());
        entity.setEstado(dto.getEstado());
        entity.setActivo(dto.getActivo());       
        entity.setUbigeoMaximo(dto.getUbigeoMaximo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportAgrupacionPoliticaReal convertirAgrupacionPoliticaReal(AgrupacionPoliticaRealDto dto) {
		ImportAgrupacionPoliticaReal entity = new ImportAgrupacionPoliticaReal();
        entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipoAgrupacionPolitica(dto.getTipoAgrupacionPolitica());
        entity.setEstado(dto.getEstado());
        entity.setActivo(dto.getActivo());       
        entity.setUbigeoMaximo(dto.getUbigeoMaximo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportDetUbigeoEleccionAgrupacionPolitica convertirUbigeoEleccionAgrupacionPolitica(DetUbigeoEleccionAgrupacionPoliticaDto dto) {
		ImportDetUbigeoEleccionAgrupacionPolitica entity = new ImportDetUbigeoEleccionAgrupacionPolitica();
        entity.setId(dto.getId());
        
        if(dto.getIdAgrupacionPolitica()!=null) {
        	ImportAgrupacionPolitica agrupacionPolitica = new ImportAgrupacionPolitica();
            agrupacionPolitica.setId(dto.getIdAgrupacionPolitica());
            entity.setAgrupacionPolitica(agrupacionPolitica);
        }
        
        
        if(dto.getIdDetUbigeoEleccion()!=null) {
        	ImportUbigeoEleccion ubigeoEleccion = new ImportUbigeoEleccion();
            ubigeoEleccion.setId(dto.getIdDetUbigeoEleccion());
            entity.setUbigeoEleccion(ubigeoEleccion);
        }
       
        entity.setActivo(dto.getActivo());
        entity.setEstado(dto.getEstado());
        entity.setPosicion(dto.getPosicion());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        
        return entity;
    }
	
	public static ImportEleccion convertirEleccion(EleccionDto dto) {	
		ImportEleccion entity = new ImportEleccion();
		entity.setId(dto.getId());
        if (dto.getIdProcesoElectoral() != null) {
        	ImportProcesoElectoral procesoElectoral = new ImportProcesoElectoral();
        	procesoElectoral.setId(dto.getIdProcesoElectoral());
            entity.setProcesoElectoral(procesoElectoral);
        }
        entity.setNombre(dto.getNombre());
        entity.setPrincipal(dto.getPrincipal());
        entity.setPreferencial(dto.getPreferencial());
        entity.setCodigo(dto.getCodigo());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	
	public static ImportLocalVotacion convertirLocalVotacion(LocalVotacionDto dto) {
		ImportLocalVotacion entity = new ImportLocalVotacion();
		entity.setId(dto.getId());
		ImportUbigeo ubigeo = new ImportUbigeo();
		ubigeo.setId(dto.getIdUbigeo());
		entity.setCodigo(dto.getCodigo());
        entity.setUbigeo(ubigeo);
        entity.setNombre(dto.getNombre());
        entity.setDireccion(dto.getDireccion());
        entity.setReferencia(dto.getReferencia());
        entity.setCentroPoblado(dto.getCentroPoblado());
        entity.setCantidadMesas(dto.getCantidadMesas());
        entity.setCantidadElectores(dto.getCantidadElectores());
        entity.setEstado(dto.getEstado());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportUbigeoEleccion convertirUbigeoEleccion(UbigeoEleccionDto dto) {
		ImportUbigeoEleccion entity = new ImportUbigeoEleccion();
		entity.setId(dto.getId());
        if (dto.getIdUbigeo() != null) {
        	ImportUbigeo ubigeo = new ImportUbigeo();
        	ubigeo.setId(dto.getIdUbigeo());
            entity.setUbigeo(ubigeo);
        }
        if (dto.getCodigoEleccion() != null) {
        	ImportEleccion eleccion = new ImportEleccion();
        	eleccion.setId(dto.getIdEleccion());
            entity.setEleccion(eleccion);
        }
        entity.setActivo(dto.getActivo());        
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportCandidato convertirCandidato(CandidatoDto dto) {
		ImportCandidato entity = new ImportCandidato();
		entity.setId(dto.getId());

        if (dto.getIdDistritoElectoral() != null) {
        	ImportDistritoElectoral distritoElectoral = new ImportDistritoElectoral();
        	distritoElectoral.setId(dto.getIdDistritoElectoral());
            entity.setDistritoElectoral(distritoElectoral);
        }

        if (dto.getCodigoEleccion() != null) {
        	ImportEleccion eleccion = new ImportEleccion();
        	eleccion.setId(dto.getIdEleccion());
            entity.setEleccion(eleccion);
        }

        if (dto.getIdAgrupacionPolitica() != null) {
        	ImportAgrupacionPolitica agrupacionPolitica = new ImportAgrupacionPolitica();
        	agrupacionPolitica.setId(dto.getIdAgrupacionPolitica());
            entity.setAgrupacionPolitica(agrupacionPolitica);
        }

        if (dto.getIdUbigeo() != null) {
        	ImportUbigeo ubigeo = new ImportUbigeo();
        	ubigeo.setId(dto.getIdUbigeo());
            entity.setUbigeo(ubigeo);
        }

        entity.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        entity.setApellidoPaterno(dto.getApellidoPaterno());
        entity.setApellidoMaterno(dto.getApellidoMaterno());
        entity.setNombres(dto.getNombres());
        entity.setEstado(dto.getEstado());
        entity.setSexo(dto.getSexo());
        entity.setLista(dto.getLista());
        entity.setCargo(dto.getCargo());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;

    }
	
	public static ImportCandidatoFicticio convertirCandidatoFicticio(CandidatoFicticioDto dto) {
		ImportCandidatoFicticio entity = new ImportCandidatoFicticio();
		entity.setId(dto.getId());
		entity.setIdDistritoElectoral(dto.getIdDistritoElectoral());
		entity.setIdEleccion(dto.getIdEleccion());
		entity.setIdAgrupacionPolitica(dto.getIdAgrupacionPolitica());
		entity.setIdUbigeo(dto.getIdUbigeo());
        entity.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        entity.setApellidoPaterno(dto.getApellidoPaterno());
        entity.setApellidoMaterno(dto.getApellidoMaterno());
        entity.setNombres(dto.getNombres());
        entity.setEstado(dto.getEstado());
        entity.setSexo(dto.getSexo());
        entity.setLista(dto.getLista());
        entity.setCargo(dto.getCargo());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;

    }
	
	
	public static ImportCandidatoReal convertirCandidatoReal(CandidatoRealDto dto) {
		ImportCandidatoReal entity = new ImportCandidatoReal();
		entity.setId(dto.getId());
		entity.setIdDistritoElectoral(dto.getIdDistritoElectoral());
		entity.setIdEleccion(dto.getIdEleccion());
		entity.setIdAgrupacionPolitica(dto.getIdAgrupacionPolitica());
		entity.setIdUbigeo(dto.getIdUbigeo());
        entity.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        entity.setApellidoPaterno(dto.getApellidoPaterno());
        entity.setApellidoMaterno(dto.getApellidoMaterno());
        entity.setNombres(dto.getNombres());
        entity.setEstado(dto.getEstado());
        entity.setSexo(dto.getSexo());
        entity.setLista(dto.getLista());
        entity.setCargo(dto.getCargo());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;

    }
	
	public static ImportDistritoElectoral convertirDistritoElectoral(DistritoElectoralDto dto) {
		ImportDistritoElectoral entity = new ImportDistritoElectoral();
		entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        if (dto.getAudFechaCreacion() != null) {
            entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        }
        return entity;

    }
	
	public static ImportFormato convertirFormato(FormatoDto dto) {
		ImportFormato entity = new ImportFormato();
        entity.setId(dto.getId());
        if (dto.getIdArchivoActa() != null) {
        	ImportArchivo archivo = new ImportArchivo();
        	archivo.setId(dto.getIdArchivoActa());
            entity.setArchivo(archivo);
        }
        entity.setCorrelativo(dto.getCorrelativo());
        entity.setTipoFormato(dto.getTipoFormato());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportProcesoElectoral convertirProcesoElectoral(ProcesoElectoralDto dto) {
		ImportProcesoElectoral entity = new ImportProcesoElectoral();
		entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setAcronimo(dto.getAcronimo());
        entity.setTipoAmbitoElectoral(dto.getTipoAmbitoElectoral());
        entity.setActivo(dto.getActivo());
        entity.setFechaConvocatoria(DateUtil.getDate(dto.getFechaConvocatoria(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportMiembroMesaSorteado convertirTabMiembroMesaSorteado(MiembroMesaSorteadoDto dto) {
		ImportMiembroMesaSorteado entity = new ImportMiembroMesaSorteado();
		entity.setId(dto.getId());
        entity.setCargo(dto.getCargo());
        entity.setBolo(dto.getBolo());
        entity.setDireccion(dto.getDireccion());
        entity.setEstado(dto.getEstado());
        entity.setAsistenciaAutomatico(dto.getAsistenciaAutomatico());
        entity.setAsistenciaManual(dto.getAsistenciaManual());
        entity.setActivo(dto.getActivo());
        entity.setTurno(dto.getTurno());

        if (dto.getIdMesa() != null) {
        	ImportMesa mesa = new ImportMesa();
        	mesa.setId(dto.getIdMesa());
            entity.setMesa(mesa);
        }

        if (dto.getIdPadronElectoral() != null) {
        	ImportPadronElectoral padron = new ImportPadronElectoral();
        	padron.setId(dto.getIdPadronElectoral());            
        	entity.setPadronElectoral(padron);
        }

        entity.setActivo(dto.getActivo());        
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportDetActaPreferencial convertirDetActaPreferencial(DetActaPreferencialDto dto) {
		ImportDetActaPreferencial entity = new ImportDetActaPreferencial();
        entity.setId(dto.getId());
        entity.setPosicion(dto.getPosicion());
        entity.setLista(dto.getLista());
        entity.setVotos(dto.getVotos());
        entity.setVotosAutomatico(dto.getVotosAutomatico());
        entity.setVotosManual1(dto.getVotosManual1());
        entity.setVotosManual2(dto.getVotosManual2());
        entity.setEstadoErrorMaterial(dto.getEstadoErrorMaterial());
        entity.setIlegible(dto.getIlegible());
        entity.setIlegiblev1(null);
        entity.setIlegiblev2(null);
        entity.setActivo(dto.getActivo());

        if (dto.getDistritoElectoralId() != null) {
        	ImportDistritoElectoral distritoElectoral = new ImportDistritoElectoral();
        	distritoElectoral.setId(dto.getDistritoElectoralId());
            entity.setDistritoElectoral(distritoElectoral);
        }

        if (dto.getDetActaId() != null) {
        	ImportDetActa detActa = new ImportDetActa();
        	detActa.setId(dto.getDetActaId());
            entity.setDetActa(detActa);
        }

        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportUsuario convertirUsuario(UsuarioDto dto) {
		ImportUsuario entity = new ImportUsuario();
		entity.setId(dto.getId());
		entity.setUsuario(dto.getNombreUsuario());
		entity.setTipoDocumentoIdentidad(dto.getTipoDocumentoIdentidad());
		entity.setDocumentoIdentidad(dto.getDocumentoIdentidad());
		entity.setPerfil(dto.getPerfil());
		entity.setCentroComputo(dto.getCentroComputo());
		entity.setSesionActiva(dto.getSesionActiva());
		entity.setActasAsignadas(dto.getActasAsignadas());
        entity.setActasAtendidas(dto.getActasAtendidas());
        entity.setActivo(dto.getActivo());
        
        entity.setIdUsuario(dto.getIdUsuario());
        entity.setAcronimoProceso(dto.getAcronimoProceso());
        entity.setNombreCentroComputo(dto.getNombreCentroComputo());
        entity.setCodigo1(dto.getClave());
        entity.setIdPerfil(dto.getIdPerfil());
        entity.setCodigo2(dto.getClaveTemporal());
        entity.setNombres(dto.getNombres());
        entity.setCorreos(dto.getCorreos());
        entity.setDesincronizadoSasa(dto.isDesincronizadoSasa());;
        entity.setApellidoPaterno(dto.getApellidoPaterno());
        entity.setApellidoMaterno(dto.getApellidoMaterno());
        entity.setPersonaAsignada(dto.getPersonaAsignada());
        
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportPuestaCero convertirPuestaCero(PuestaCeroDto dto) {
		ImportPuestaCero entity = new ImportPuestaCero();
        entity.setId(dto.getId());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        if (dto.getAudFechaCreacion() != null) {
            entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        }
        return entity;
    }
	
	public static ImportCabActaFormato convertirCabActaFormato(CabActaFormatoDto dto) {
		ImportCabActaFormato entity = new ImportCabActaFormato();
        entity.setId(dto.getId());
        if (dto.getIdFormato()!= null) {
        	ImportFormato formato = new ImportFormato();
        	formato.setId(dto.getIdFormato());
            entity.setFormato(formato);        
        }
        if (dto.getIdArchivo() != null) {
        	ImportArchivo archivo = new ImportArchivo();
        	archivo.setId(dto.getIdArchivo());
        	entity.setArchivoFormatoPdf(archivo);
        }
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportDetActaFormato convertirDetActaFormato(DetActaFormatoDto dto) {
		ImportDetActaFormato entity = new ImportDetActaFormato();
        entity.setId(dto.getId());
        if (dto.getIdActa() != null) {
        	ImportActa acta = new ImportActa();
        	acta.setId(dto.getIdActa());
            entity.setActa(acta);
        }
        if (dto.getIdCabActaFormato() != null) {
        	ImportCabActaFormato cabActaFormato = new ImportCabActaFormato();
        	cabActaFormato.setId(dto.getIdCabActaFormato());
        	entity.setCabActaFormato(cabActaFormato);
        }
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportMesa convertirMesa(MesaDto dto) {
		ImportMesa entity = new ImportMesa();
		entity.setId(dto.getId());
        if (dto.getIdLocalVotacion() != null) {
        	ImportLocalVotacion localVotacion = new ImportLocalVotacion();
        	localVotacion.setId(dto.getIdLocalVotacion());            
        	entity.setLocalVotacion(localVotacion);
        }
        entity.setCodigo(dto.getCodigo());
        entity.setCantidadElectoresHabiles(dto.getCantidadElectoresHabiles());
        entity.setCantidadElectoresHabilesExtranjeros(dto.getCantidadElectoresHabilesExtranjeros());
        entity.setDiscapacidad(dto.getDiscapacidad());
        entity.setSolucionTecnologica(dto.getSolucionTecnologica());
        entity.setEstadoMesa(dto.getEstadoMesa());
        entity.setEstadoDigitalizacionLe(dto.getEstadoDigitalizacionLe());
        entity.setEstadoDigitalizacionMm(dto.getEstadoDigitalizacionMm());
        entity.setActivo(dto.getActivo());       
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportActa convertirCabActa(CabActaDto dto) {
		ImportActa entity = new ImportActa();
		entity.setId(dto.getId());
		
        if (dto.getIdDetUbigeoEleccion() != null) {
        	ImportUbigeoEleccion ubigeoEleccion = new ImportUbigeoEleccion();
        	ubigeoEleccion.setId(dto.getIdDetUbigeoEleccion());
            entity.setUbigeoEleccion(ubigeoEleccion);
        }

        if (dto.getIdMesa() != null) {
        	ImportMesa mesa = new ImportMesa();
        	mesa.setId(dto.getIdMesa());
            entity.setMesa(mesa);
        }

        entity.setNumeroCopia(dto.getNumeroCopia());
        entity.setNumeroLote(dto.getNumeroLote());
        entity.setDigitoChequeoEscrutinio(dto.getDigitoChequeoEscrutinio());
        entity.setDigitoChequeoInstalacion(dto.getDigitoChequeoInstalacion());
        entity.setDigitoChequeoSufragio(dto.getDigitoChequeoSufragio());
        entity.setTipoLote(dto.getTipoLote());
        entity.setElectoresHabiles(dto.getElectoresHabiles());
        entity.setCvas(dto.getCvas());
        entity.setVotosCalculados(dto.getVotosCalculados());
        entity.setTotalVotos(dto.getTotalVotos());
        entity.setEstadoActa(dto.getEstadoActa());
        entity.setEstadoCc(dto.getEstadoCc());
        entity.setEstadoActaResolucion(dto.getEstadoActaResolucion());
        entity.setEstadoDigitalizacion(dto.getEstadoDigitalizacion());
        entity.setEstadoErrorMaterial(dto.getEstadoErrorMaterial());
        entity.setDigitalizacionEscrutinio(dto.getDigitalizacionEscrutinio());
        entity.setDigitalizacionInstalacionSufragio(dto.getDigitalizacionInstalacionSufragio());
        entity.setControlDigEscrutinio(dto.getControlDigEscrutinio());
        entity.setControlDigInstalacionSufragio(dto.getControlDigInstalacionSufragio());
        entity.setObservDigEscrutinio(dto.getObservDigEscrutinio());
        entity.setObservDigInstalacionSufragio(dto.getObservDigInstalacionSufragio());
        entity.setDigitacionHoras(dto.getDigitacionHoras());
        entity.setDigitacionVotos(dto.getDigitacionVotos());
        entity.setDigitacionObserv(dto.getDigitacionObserv());
        entity.setDigitacionFirmasAutomatico(dto.getDigitacionFirmasAutomatico());
        entity.setDigitacionFirmasManual(dto.getDigitacionFirmasManual());
        entity.setControlDigitacion(dto.getControlDigitacion());
        entity.setHoraEscrutinioAutomatico(dto.getHoraEscrutinioAutomatico());
        entity.setHoraEscrutinioManual(dto.getHoraEscrutinioManual());
        entity.setHoraInstalacionAutomatico(dto.getHoraInstalacionAutomatico());
        entity.setHoraInstalacionManual(dto.getHoraInstalacionManual());
        entity.setDescripcionObservAutomatico(dto.getDescripcionObservAutomatico());
        entity.setEscrutinioFirmaMm1Automatico(dto.getEscrutinioFirmaMm1Automatico());
	    entity.setIlegibleCvas(dto.getIlegibleCvas());
	    entity.setSolucionTecnologica(dto.getSolucionTecnologica());
        entity.setEscrutinioFirmaMm2Automatico(dto.getEscrutinioFirmaMm2Automatico());
        entity.setEscrutinioFirmaMm3Automatico(dto.getEscrutinioFirmaMm3Automatico());
        entity.setInstalacionFirmaMm1Automatico(dto.getInstalacionFirmaMm1Automatico());
        entity.setInstalacionFirmaMm2Automatico(dto.getInstalacionFirmaMm2Automatico());
        entity.setInstalacionFirmaMm3Automatico(dto.getInstalacionFirmaMm3Automatico());
        entity.setSufragioFirmaMm1Automatico(dto.getSufragioFirmaMm1Automatico());
        entity.setSufragioFirmaMm2Automatico(dto.getSufragioFirmaMm2Automatico());
        entity.setSufragioFirmaMm3Automatico(dto.getSufragioFirmaMm3Automatico());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportDetActa convertirDetActa(DetActaDto dto) {
		ImportDetActa entity = new ImportDetActa();
        entity.setId(dto.getId());
        if (dto.getIdCabActa() != null) {
        	ImportActa acta = new ImportActa();
        	acta.setId(dto.getIdCabActa());
            entity.setActa(acta);
        }
        

        if (dto.getIdAgrupacionPolitica() != null) {
        	ImportAgrupacionPolitica agrupacionPolitica = new ImportAgrupacionPolitica();
        	agrupacionPolitica.setId(dto.getIdAgrupacionPolitica());
            entity.setAgrupacionPolitica(agrupacionPolitica);
        }

        entity.setPosicion(dto.getPosicion());
        entity.setVotos(dto.getVotos());
        entity.setEstadoErrorMaterial(dto.getEstadoErrorMaterial());
        entity.setActivo(dto.getActivo());        
        entity.setIlegible(dto.getIlegible());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
	
	public static ImportUbigeo convertirUbigeo(UbigeoDto dto) {
		
		ImportUbigeo entity = new ImportUbigeo();
        entity.setId(dto.getId());
        if (dto.getIdCentroComputo() != null) {
        	ImportCentroComputo centroComputo = new ImportCentroComputo();
        	centroComputo.setId(dto.getIdCentroComputo());
            entity.setCentroComputo(centroComputo);
        }
        if (dto.getIdAmbitoElectoral() != null) {
        	ImportAmbitoElectoral ambitoElectoral = new ImportAmbitoElectoral();
        	ambitoElectoral.setId(dto.getIdAmbitoElectoral());
            entity.setAmbitoElectoral(ambitoElectoral);
        }

        if (dto.getIdDistritoElectoral() != null) {
        	ImportDistritoElectoral distritoElectoral = new ImportDistritoElectoral();
        	distritoElectoral.setId(dto.getIdDistritoElectoral());            
        	entity.setDistritoElectoral(distritoElectoral);
        }
        if(dto.getIdPadre()!=null) {
        	ImportUbigeo ubigeoPadre = new ImportUbigeo();
        	ubigeoPadre.setId(dto.getIdPadre());
        	entity.setUbigeoPadre(ubigeoPadre);
        }

        entity.setNombre(dto.getNombre());
        entity.setCodigo(dto.getCodigo());
        entity.setDepartamento(dto.getDepartamento());
        entity.setProvincia(dto.getProvincia());
        entity.setTipoAmbitoGeografico(dto.getTipoAmbitoGeografico());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        if (dto.getAudFechaCreacion() != null) {
            entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        }
        return entity;
    }
	
	public static ImportOrcCatalogo convertirCabCatalogo(CatalogoDto dto) {
		ImportOrcCatalogo entity = new ImportOrcCatalogo();
		entity.setId(dto.getId().intValue());
        entity.setMaestro(dto.getMaestro());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }

    public static ImportOrcDetalleCatalogoReferencia convertirDetCatalogoReferencia(DetCatalogoReferenciaDto dto) {
    	ImportOrcDetalleCatalogoReferencia entity = new ImportOrcDetalleCatalogoReferencia();
    	entity.setId(dto.getId().intValue());
        if (dto.getIdCatalogo() != null) {
        	ImportOrcCatalogo catalogo = new ImportOrcCatalogo();
        	catalogo.setId(dto.getIdCatalogo().intValue());            
        	entity.setCatalogo(catalogo);
        }
        entity.setTablaReferencia(dto.getTablaReferencia());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }

    public static ImportOrcDetalleCatalogoEstructura convertirDetCatalogoEstructura(DetCatalogoEstructuraDto dto) {
    	ImportOrcDetalleCatalogoEstructura entity = new ImportOrcDetalleCatalogoEstructura();
    	entity.setId(dto.getId().intValue());
        if (dto.getIdCatalogo() != null) {
        	ImportOrcCatalogo catalogo = new ImportOrcCatalogo();
        	catalogo.setId(dto.getIdCatalogo().intValue());            
        	entity.setCatalogo(catalogo);
        }
        entity.setColumna(dto.getColumna());
        entity.setNombre(dto.getNombre());
        entity.setCodigoI(dto.getCodigoI());
        entity.setCodigoS(dto.getCodigoS());
        entity.setOrden(dto.getOrden().intValue());
        entity.setTipo(dto.getTipo());
        entity.setInformacionAdicional(dto.getInformacionAdicional());
        entity.setObligatorio(dto.getObligatorio());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        if (dto.getAudFechaCreacion() != null) {
            entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        }

        return entity;
    }
    
    public static ImportArchivo convertirTabArchivo(ArchivoDto dto) {
    	ImportArchivo entity = new ImportArchivo();
    	entity.setId(dto.getId());
        entity.setGuid(dto.getGuid());
        entity.setNombre(dto.getNombre());
        entity.setNombreOriginal(dto.getNombreOriginal());
        entity.setFormato(dto.getFormato());
        entity.setPeso(dto.getPeso());
        entity.setRuta(dto.getRuta());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    
    public static ImportDetalleConfiguracionDocumentoElectoralHistorial convertirAdmDetConfiguracionDocumentoElectoralHistorial(AdmDetConfigDocElectoralHistDto dto) {
    	ImportDetalleConfiguracionDocumentoElectoralHistorial entity = new ImportDetalleConfiguracionDocumentoElectoralHistorial();
    	entity.setId(dto.getId().intValue());

        if (dto.getIdSeccion() != null) {
        	ImportSeccion seccion = new ImportSeccion();
        	seccion.setId(dto.getIdSeccion().intValue());
            entity.setSeccion(seccion);
        }

        if (dto.getIdDetalleTipoEleccionDocumentoElectoral() != null) {
        	ImportDetalleTipoEleccionDocumentoElectoralHistorial dtedeh = new ImportDetalleTipoEleccionDocumentoElectoralHistorial();
        	dtedeh.setId(dto.getIdDetalleTipoEleccionDocumentoElectoral().intValue());
            entity.setDetalleTipoEleccionDocumentoElectoralHistorial(dtedeh);
        }

        entity.setTipoDato(dto.getTipoDato());
        entity.setPixelTopX(dto.getPixelTopX()); // BigDecimal
        entity.setPixelTopY(dto.getPixelTopY()); // BigDecimal
        entity.setPixelBottomX(dto.getPixelBottomX()); // BigDecimal
        entity.setPixelBottomY(dto.getPixelBottomY()); // BigDecimal
        entity.setCoordenadaRelativaTopX(dto.getCoordenadaRelativaTopX()); // BigDecimal
        entity.setCoordenadaRelativaTopY(dto.getCoordenadaRelativaTopY()); // BigDecimal
        entity.setCoordenadaRelativaBottomX(dto.getCoordenadaRelativaBottomX()); // BigDecimal
        entity.setCoordenadaRelativaBottomY(dto.getCoordenadaRelativaBottomY()); // BigDecimal
        entity.setHabilitado(dto.getHabilitado());
        entity.setActivo(dto.getActivo());
        entity.setCorrelativo(dto.getCorrelativo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }

    public static ImportDetalleTipoEleccionDocumentoElectoralHistorial convertirAdmDetTipoEleccionDocumentoElectoralHistorial(AdmDetTipoEleccionDocElectoralHistDto dto) {
    	ImportDetalleTipoEleccionDocumentoElectoralHistorial entity = new ImportDetalleTipoEleccionDocumentoElectoralHistorial();
    	entity.setId(dto.getId().intValue());


        if (dto.getIdEleccion() != null) {
        	ImportEleccion eleccion = new ImportEleccion();
        	eleccion.setId(dto.getIdEleccion());
            entity.setEleccion(eleccion);
        }
        
        if (dto.getIdProcesoElectoral() != null) {
        	ImportProcesoElectoral proceso = new ImportProcesoElectoral();
        	proceso.setId(dto.getIdProcesoElectoral());
            entity.setProcesoElectoral(proceso);
        }

        if (dto.getIdDocumentoElectoral() != null) {
        	ImportDocumentoElectoral documentoElectoral = new ImportDocumentoElectoral();
        	documentoElectoral.setId(dto.getIdDocumentoElectoral().intValue());            
        	entity.setDocumentoElectoral(documentoElectoral);
        }

        entity.setActivo(dto.getActivo());
        entity.setRangoInicial(dto.getRangoInicial());
        entity.setRangoFinal(dto.getRangoFinal());
        entity.setDigitoChequeo(dto.getDigitoChequeo());
        entity.setDigitoError(dto.getDigitoError());
        entity.setRequerido(dto.getRequerido());
        entity.setCorrelativo(dto.getCorrelativo().intValue());
        entity.setRequerido(dto.getRequerido());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }

    public static ImportDocumentoElectoral convertirAdmTabDocumentoElectoral(AdmDocElectoralDto dto) {
    	ImportDocumentoElectoral entity = new ImportDocumentoElectoral();
    	
    	entity.setId(dto.getId().intValue());
    	
    	if(dto.getIdPadre()!=null) {
    		ImportDocumentoElectoral padre = new ImportDocumentoElectoral();
    		padre.setId(dto.getIdPadre().intValue());
    		entity.setDocumentoElectoralPadre(padre);
    	}
    	
    	
        entity.setNombre(dto.getNombre());
        entity.setVisible(dto.getVisible());
        entity.setAbreviatura(dto.getAbreviatura());
        entity.setTipoImagen(dto.getTipoImagen());
        entity.setEscanerAmbasCaras(dto.getEscanerAmbasCaras());
        entity.setMultipagina(dto.getMultipagina());
        entity.setTamanioHoja(dto.getTamanioHoja());
        entity.setCodigoBarraPixelTopX(dto.getCodigoBarraPixelTopX()); // string
        entity.setCodigoBarraPixelTopY(dto.getCodigoBarraPixelTopY()); // string
        entity.setCodigoBarraPixelBottomX(dto.getCodigoBarraPixelBottomX()); // string
        entity.setCodigoBarraPixelBottomY(dto.getCodigoBarraPixelBottomY()); // string
        entity.setCodigoBarraCoordenadaRelativaTopX(dto.getCodigoBarraCoordenadaRelativaTopX()); // string
        entity.setCodigoBarraCoordenadaRelativaTopY(dto.getCodigoBarraCoordenadaRelativaTopY()); // string
        entity.setCodigoBarraCoordenadaRelativaBottomX(dto.getCodigoBarraCoordenadaRelativaBottomX()); // string
        entity.setCodigoBarraCoordenadaRelativaBottomY(dto.getCodigoBarraCoordenadaRelativaBottomY()); // string
        entity.setCodigoBarraWidth(dto.getCodigoBarraWidth()); // string
        entity.setCodigoBarraHeight(dto.getCodigoBarraHeight()); // string
        entity.setCodigoBarraOrientacion(dto.getCodigoBarraOrientacion());
        entity.setConfiguracionGeneral(dto.getConfiguracionGeneral());
        entity.setActivo(dto.getActivo());        
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }

    public static ImportSeccion convertirAdmTabSeccion(AdmSeccionDto dto) {
    	ImportSeccion entity = new ImportSeccion();
    	entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setAbreviatura(dto.getAbreviatura());
        entity.setActivo(dto.getActivo());
        entity.setOrientacion(dto.getOrientacion());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    public static ImportVersion convertirTabVersion(VersionDto dto) {
    	ImportVersion entity = new ImportVersion();
    	entity.setId(dto.getId());        
    	entity.setCadena(dto.getCadena());
        entity.setCodversion(dto.getCodversion());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        //entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        entity.setFechaVersion(DateUtil.getDate(dto.getFechaVersion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    public static ImportPadronElectoral convertirPadron(MaePadronDto dto) {
        return ImportPadronElectoral.builder()
                .id(dto.getId())
                .idMesa(dto.getIdMesa())
                .codigoMesa(dto.getCodigoMesa())
                .apellidoMaterno(dto.getApellidoMaterno())
                .apellidoPaterno(dto.getApellidoPaterno())
                .orden(dto.getOrden())
                .nombres(dto.getNombres())
                .ubigeo(dto.getUbigeo())
                .ubigeoReniec(dto.getUbigeoReniec())
                .idTipoDocumentoIdentidad(dto.getIdTipoDocumentoIdentidad())                
                .documentoIdentidad(dto.getDocumentoIdentidad())
                .sexo(dto.getSexo())
                .vd(dto.getVd())
                .activo(dto.getActivo())
                .usuarioCreacion(dto.getAudUsuarioCreacion())
                .usuarioModificacion(dto.getAudUsuarioModificacion())
                .fechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH))
                .fechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH))
                .build();
    }
    
    public static ImportOpcionVoto convertirOpcionVoto(OpcionVotoDto dto) {
    	ImportOpcionVoto entity = new ImportOpcionVoto();
    	entity.setId(dto.getId());        
    	entity.setCodigo(dto.getCodigo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPosicion(dto.getPosicion());        
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    public static ImportDetActaOpcion convertirDetActaOpcion(DetActaOpcionDto dto) {
    	ImportDetActaOpcion entity = new ImportDetActaOpcion();
    	entity.setId(dto.getId());    
    	entity.setIdDetActaOpcionCc(dto.getIdDetActaOpcionCc());   	
    	
    	if(dto.getIdDetActa()!=null) {
    		ImportDetActa detActa = new ImportDetActa();
    		detActa.setId(dto.getIdDetActa());   		
    		entity.setDetActa(detActa);   	
    	}
    	
    	entity.setVotos(dto.getVotos());
    	entity.setVotosAutomatico(dto.getVotosAutomatico());
    	entity.setVotosManual1(dto.getVotosManual1());   
    	entity.setVotosManual2(dto.getVotosManual2());
    	entity.setPosicion(dto.getPosicion());
    	entity.setEstadoErrorMaterial(dto.getEstadoErrorMaterial());     
    	entity.setIlegible(dto.getIlegible());   	
    	entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    
    public static ImportCabParametro convertirCabParametro(CabParametroDto dto) {
    	ImportCabParametro entity = new ImportCabParametro();
    	entity.setId(dto.getId());    
    	entity.setParametro(dto.getParametro());
    	entity.setPerfilesAutorizados(dto.getPerfilesAutorizados());
    	entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    public static ImportDetParametro convertirDetParametro(DetParametroDto dto) {
    	ImportDetParametro entity = new ImportDetParametro();
    	entity.setId(dto.getId());    
    	
    	if(dto.getIdParametro()!=null) {
    		ImportCabParametro cabEntity = new ImportCabParametro();
    		cabEntity.setId(dto.getIdParametro());   		
    		entity.setParametro(cabEntity);  	
    	}
    	
    	entity.setDescripcion(dto.getDescripcion());
    	entity.setTipoDato(dto.getTipoDato());
    	entity.setNombre(dto.getNombre());    	
    	entity.setActivo(dto.getActivo());
    	entity.setValor(dto.getValor());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    public static ImportDetDistritoElectoralEleccion convertirDetDistritoElectoralEleccion(DetDistritoElectoralEleccionDto dto) {
    	ImportDetDistritoElectoralEleccion entity = new ImportDetDistritoElectoralEleccion();
    	entity.setId(dto.getId());    
    	
    	if(dto.getIdDistritoElectoral()!=null) {
    		ImportDistritoElectoral deEntity = new ImportDistritoElectoral();
    		deEntity.setId(dto.getIdDistritoElectoral());   		
    		entity.setDistritoElectoral(deEntity);	
    	}
    	
    	if(dto.getIdEleccion()!=null) {
    		ImportEleccion eleccionEntity = new ImportEleccion();
    		eleccionEntity.setId(dto.getIdEleccion());   		
    		entity.setEleccion(eleccionEntity);	
    	}
    	
    	entity.setCantidadCandidatos(dto.getCantidadCandidatos());
    	entity.setCantidadCurules(dto.getCantidadCurules());
    	entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getAudUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    public static ImportTabJuradoElectoralEspecial convertirJuradoElectoralEspecial(TabJuradoElectoralEspecialDto dto){
    	ImportTabJuradoElectoralEspecial entity = new ImportTabJuradoElectoralEspecial();
    	entity.setId(dto.getId());    
    	entity.setCodigoCentroComputo(dto.getCodigoCentroComputo());
    	entity.setApellidoMaternoRepresentante(dto.getApellidoMaternoRepresentante());
    	entity.setApellidoPaternoRepresentante(dto.getApellidoMaternoRepresentante());
    	entity.setDireccion(dto.getDireccion());
    	entity.setNombre(dto.getNombre());
    	entity.setNombresRepresentante(dto.getNombresRepresentante());
    	entity.setIdJee(dto.getIdJee());    	
    	entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getUsuarioCreacion());
        entity.setUsuarioModificacion(dto.getUsuarioModificacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        //entity.setFechaModificacion(DateUtil.getDate(dto.getFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
    public static ImportVersionModelo convertirTabVersionModelo(VersionModeloDto dto) {
    	ImportVersionModelo entity = new ImportVersionModelo();
    	entity.setId(dto.getId());        
    	entity.setCadena(dto.getCadena());
        entity.setCodversion(dto.getCodversion());
        entity.setActivo(dto.getActivo());
        entity.setUsuarioCreacion(dto.getAudUsuarioCreacion());
        entity.setFechaCreacion(DateUtil.getDate(dto.getAudFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        entity.setFechaModificacion(DateUtil.getDate(dto.getAudFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        entity.setFechaVersion(DateUtil.getDate(dto.getFechaVersion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
        return entity;
    }
    
}
