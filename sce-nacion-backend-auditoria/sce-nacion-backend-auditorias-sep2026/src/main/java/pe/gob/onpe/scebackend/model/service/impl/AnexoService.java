package pe.gob.onpe.scebackend.model.service.impl;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.funciones.AnexoListaActaObservadaDTO;
import pe.gob.onpe.scebackend.model.dto.funciones.AnexoTiposDTO;
import pe.gob.onpe.scebackend.model.dto.funciones.AnexosGeneralDTO;
import pe.gob.onpe.scebackend.model.dto.request.AnexosRequestDto;
import pe.gob.onpe.scebackend.model.orc.repository.AnexosRepository;
import pe.gob.onpe.scebackend.model.service.IAnexosService;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AnexoService implements IAnexosService {

  private static final String DELIMI = ";";
  public static final String IDTIPOELECCION = "IDTIPOELECCION";
  public static final String IDODPE = "IDODPE";
  public static final String TXUBIGEO = "TXUBIGEO";
  public static final String TXORGPOLITICA = "TXORGPOLITICA";
  public static final String IDORGPOLITICA = "IDORGPOLITICA";
  public static final String C_CODIGO_ELECCION = "c_codigo_eleccion";
  public static final String C_CODIGO_UBIGEO = "c_codigo_ubigeo";
  public static final String C_CODIGO_OP = "c_codigo_op";
  public static final String C_NOMBRE_OP = "c_nombre_op";
  public static final String C_CODIGO_AMBITO_ELECTORAL = "c_codigo_ambito_electoral";

  private final AnexosRepository anexosRepository;

    public AnexoService(AnexosRepository anexosRepository) {
        this.anexosRepository = anexosRepository;
    }

    @Override
  public List<AnexoListaActaObservadaDTO> listaActaObservada(AnexosRequestDto request) {
    List<Map<String, Object>> lista =
        this.anexosRepository.listaActasObservadas(request.getEsquema(), request.getCentroComputo(), request.getUsuarioConsulta());
    return lista.stream().map(this::mapListaObservadas).toList();
  }

  @Override
  public AnexosGeneralDTO anexo1(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<AnexoListaActaObservadaDTO> listaActasObservadas =
        this.anexosRepository.listaActasObservadas(request.getEsquema(), request.getCentroComputo(), request.getUsuarioConsulta())
            .stream().map(this::mapListaObservadas).toList();
    List<AnexoTiposDTO> listaTipoObservaciones =
        this.anexosRepository.listaTipoObservacion(request.getEsquema(), request.getUsuarioConsulta())
            .stream().map(this::mapTipoObservacion).toList();
    List<AnexoTiposDTO> listaTipoErrorMaterial =
        this.anexosRepository.listaTipoErrorMaterial(request.getEsquema(), request.getUsuarioConsulta())
            .stream().map(this::mapTipoErrorMaterial).toList();
    final String[] namesTxt = {"ONPE_TBL_ACTAS_OBSERVADAS.txt", "ONPE_TBL_TIPO_OBS.txt", "ONPE_TBL_TIPO_ERR.txt"};
    createDataActasObservadas(listaActasObservadas, namesTxt[0]);
    createDataTipos(listaTipoObservaciones, namesTxt[1]);
    createDataTipos(listaTipoErrorMaterial, namesTxt[2]);
    List<File> listFiles = Arrays.stream(namesTxt).map(File::new).toList();
    File zipFile = createZipFile(listFiles, SceConstantes.NAME_ZIP_ANEXO1);
    anexoGenral.setFiles(listFiles);
    anexoGenral.setByteFile(convertFileToByteArray(zipFile));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO votos(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaVotos =
        this.anexosRepository.listaVotos(request.getEsquema(), request.getCentroComputo(), request.getUsuarioConsulta());
    createDataVotos(listaVotos, SceConstantes.NAME_TXT_VOTOS);
    File fi = new File(SceConstantes.NAME_TXT_VOTOS);
    anexoGenral.setFile(fi);
    anexoGenral.setByteFile(convertFileToByteArray(fi));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO votosCifras(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaVotos =
        this.anexosRepository.listaVotosCifras(request.getEsquema(), request.getCentroComputo(), request.getUsuarioConsulta());
    createDataVotosCifras(listaVotos, SceConstantes.NAME_TXT_VOTOS_CIFRA);
    File fi = new File(SceConstantes.NAME_TXT_VOTOS_CIFRA);
    anexoGenral.setFile(fi);
    anexoGenral.setByteFile(convertFileToByteArray(fi));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO votosCifrasInicial(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaVotos =
        this.anexosRepository.listaVotosCifrasInicial(request.getEsquema(), request.getCentroComputo(), request.getUsuarioConsulta());
    createDataVotosCifras(listaVotos, SceConstantes.NAME_TXT_VOTOS_CIFRA_INICIAL);
    File fi = new File(SceConstantes.NAME_TXT_VOTOS_CIFRA_INICIAL);
    anexoGenral.setFile(fi);
    anexoGenral.setByteFile(convertFileToByteArray(fi));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO tablaActas(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaTablaActas =
        this.anexosRepository.listaActasContabilizadas(request.getEsquema(), request.getCentroComputo(), request.getUsuarioConsulta());
    createDataTablaActas(listaTablaActas, SceConstantes.NAME_TXT_TABLA_ACTAS);
    File fi = new File(SceConstantes.NAME_TXT_TABLA_ACTAS);
    anexoGenral.setFile(fi);
    anexoGenral.setByteFile(convertFileToByteArray(fi));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO mesasNoinstaladas(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaTablaActas =
        this.anexosRepository.listaMesasNoinstaladas(request.getEsquema(), request.getCentroComputo(), request.getUsuarioConsulta());
    createDataMesasNoInstaladas(listaTablaActas, SceConstantes.NAME_TXT_MESAS_NO_INSTALADAS);
    File fi = new File(SceConstantes.NAME_TXT_MESAS_NO_INSTALADAS);
    anexoGenral.setFile(fi);
    anexoGenral.setByteFile(convertFileToByteArray(fi));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO maestraOrganizacionPolitica(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaMaestroOrganizacion =
        this.anexosRepository.listaAgrupacionPolitica(request.getEsquema(), request.getUsuarioConsulta());
    List<Map<String, Object>> listaOrdenOrganizacion =
        this.anexosRepository.listaOrdenAgrupacionPolitica(request.getEsquema(), request.getUsuarioConsulta());
    List<Map<String, Object>> listaTipoEleccion =
        this.anexosRepository.listaTipoEleccion(request.getEsquema(), request.getUsuarioConsulta());

    final String[] namesTxt =
        {"ONPE_TBL_ORGPOLITICA.txt", "ONPE_TBL_ORDENORGPOL.txt", "ONPE_TBL_TIPOELECCION.txt"};
    createDataMaestroOrganizacion(listaMaestroOrganizacion, namesTxt[0]);
    createDataOrdenOrganizacion(listaOrdenOrganizacion, namesTxt[1]);
    createDataTipoEleccion(listaTipoEleccion, namesTxt[2]);
    List<File> listFiles = Arrays.stream(namesTxt).map(File::new).toList();
    File zipFile = createZipFile(listFiles, SceConstantes.NAME_ZIP_MAESTRAS_ORG);
    anexoGenral.setFiles(listFiles);
    anexoGenral.setByteFile(convertFileToByteArray(zipFile));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO maestroUbigeo(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaTablaActas =
        this.anexosRepository.listaMaestroUbigeo(request.getEsquema(), request.getUsuarioConsulta());
    createDataMaestroUbigeo(listaTablaActas, SceConstantes.NAME_TXT_UBIGEO);
    File fi = new File(SceConstantes.NAME_TXT_UBIGEO);
    anexoGenral.setFile(fi);
    anexoGenral.setByteFile(convertFileToByteArray(fi));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO odpe(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    List<Map<String, Object>> listaTablaActas = this.anexosRepository.listaOdpe(request.getEsquema(), request.getUsuarioConsulta());
    createDataOdpe(listaTablaActas, SceConstantes.NAME_TXT_ODPE);
    File fi = new File(SceConstantes.NAME_TXT_ODPE);
    anexoGenral.setFile(fi);
    anexoGenral.setByteFile(convertFileToByteArray(fi));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO anexo2(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    AnexosGeneralDTO votos = this.votos(request);

    AnexosGeneralDTO actas = this.tablaActas(request);
    AnexosGeneralDTO mesasnoInstaladas = this.mesasNoinstaladas(request);
    AnexosGeneralDTO orgMaestra = this.maestraOrganizacionPolitica(request);
    AnexosGeneralDTO ubigeo = this.maestroUbigeo(request);
    AnexosGeneralDTO odpe = this.odpe(request);
    AnexosGeneralDTO cifras = this.votosCifras(request);
    AnexosGeneralDTO cifraInicial = this.votosCifrasInicial(request);

    List<File> listFiles = new ArrayList<>();
    listFiles.add(votos.getFile());
    listFiles.add(actas.getFile());
    listFiles.add(mesasnoInstaladas.getFile());
    listFiles.addAll(orgMaestra.getFiles());
    listFiles.add(ubigeo.getFile());
    listFiles.add(odpe.getFile());
    listFiles.add(cifras.getFile());
    listFiles.add(cifraInicial.getFile());
    File zipFile = createZipFile(listFiles, SceConstantes.NAME_ZIP_ANEXO2);
    anexoGenral.setFiles(listFiles);
    anexoGenral.setByteFile(convertFileToByteArray(zipFile));
    return anexoGenral;
  }

  @Override
  public AnexosGeneralDTO all(AnexosRequestDto request) throws GenericException, IOException {
    AnexosGeneralDTO anexoGenral = new AnexosGeneralDTO();
    AnexosGeneralDTO anexo1 = this.anexo1(request);
    AnexosGeneralDTO votos = this.votos(request);
    AnexosGeneralDTO votosCifras = this.votosCifras(request);
    AnexosGeneralDTO votosCifraInicial = this.votosCifrasInicial(request);
    AnexosGeneralDTO actas = this.tablaActas(request);
    AnexosGeneralDTO mesasnoInstaladas = this.mesasNoinstaladas(request);
    AnexosGeneralDTO orgMaestra = this.maestraOrganizacionPolitica(request);
    AnexosGeneralDTO ubigeo = this.maestroUbigeo(request);
    AnexosGeneralDTO odpe = this.odpe(request);

    List<File> listFiles = new ArrayList<>();
    listFiles.addAll(anexo1.getFiles());
    listFiles.add(votos.getFile());
    listFiles.add(votosCifras.getFile());
    listFiles.add(votosCifraInicial.getFile());
    listFiles.add(actas.getFile());
    listFiles.add(mesasnoInstaladas.getFile());
    listFiles.addAll(orgMaestra.getFiles());
    listFiles.add(ubigeo.getFile());
    listFiles.add(odpe.getFile());
    File zipFile = createZipFile(listFiles, SceConstantes.NAME_ZIP_ALL);
    anexoGenral.setFiles(listFiles);
    anexoGenral.setByteFile(convertFileToByteArray(zipFile));
    return anexoGenral;
  }

  private void createDataActasObservadas(List<AnexoListaActaObservadaDTO> listaActasObservadas, final String nombreTxt) throws IOException {
    StringBuilder txtActasObservadas = new StringBuilder();

    txtActasObservadas.append(IDTIPOELECCION).append(DELIMI)
            .append(IDODPE).append(DELIMI)
            .append("IDCENTROCOMPUTO").append(DELIMI)
            .append("NULOTE").append(DELIMI)
            .append(TXUBIGEO).append(DELIMI)
            .append("IDACTA").append(DELIMI)
            .append("IDACTACOPIA").append(DELIMI)
            .append("TXOBSACTA").append(DELIMI)
            .append("TXTIPOERRORMATERIAL").append(DELIMI)
            .append("TXDETERRORMATERIAL")
            .append("\n");

    listaActasObservadas.forEach(actas -> 
      txtActasObservadas.append(validarCampo(actas.getTipoeleccion())).append(DELIMI)
              .append(validarCampo(actas.getCodigoambito())).append(DELIMI)
              .append(validarCampo(actas.getCentrocomputo())).append(DELIMI)
              .append(validarCampo(actas.getNumerolote())).append(DELIMI)
              .append(validarCampo(actas.getUbigeo())).append(DELIMI)
              .append(validarCampo(actas.getActa())).append(DELIMI)
              .append(validarCampo(actas.getNumerocopia())).append(DELIMI)
              .append(validarCampo(actas.getEstadoacta())).append(DELIMI)
              .append(validarCampo(actas.getEstadoerrormaterial())).append(DELIMI)
              .append(validarCampo(actas.getDetalleerrormaterial()))
              .append("\n")
    );
    createTextFile(nombreTxt, txtActasObservadas);
  }

  private void createDataTipos(final List<AnexoTiposDTO> lista, final String nombreTxt) throws IOException {

    StringBuilder txtTipos = new StringBuilder();
    txtTipos.append("TXCODIGO").append(DELIMI).append("TXDESCRIPCION").append("\n");
    lista.forEach(tipo -> 
      txtTipos.append(validarCampo(tipo.getCodigo()))
              .append(DELIMI)
              .append(validarCampo(tipo.getNombre()))
              .append("\n")
    );
    createTextFile(nombreTxt, txtTipos);
  }

  private void createDataVotos(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtVotos = new StringBuilder();
    txtVotos.append(IDTIPOELECCION).append(DELIMI)
            .append(TXUBIGEO).append(DELIMI)
            .append(IDORGPOLITICA).append(DELIMI)
            .append(TXORGPOLITICA).append(DELIMI)
            .append(IDODPE).append(DELIMI)
            .append("IDDISTRITOELECTORAL").append(DELIMI)
            .append("TXDISTRITOELECTORAL").append(DELIMI)
            .append("NUBIC_AGRUPOL").append(DELIMI)
            .append("NUVOTOS").append(DELIMI)
            .append("POVOTOSVALIDOS").append(DELIMI)
            .append("POVOTOSEMITIDOS").append(DELIMI)
            .append("NUVOTOSSI").append(DELIMI)
            .append("NUVOTOSNO").append(DELIMI)
            .append("NUVOTOSBL").append(DELIMI)
            .append("NUVOTOSNU").append(DELIMI)
            .append("NUVOTOSIMP").append(DELIMI)
            .append("NELECHABIL")
            .append("\n");

    lista.forEach(voto -> 
      txtVotos.append(String.join(DELIMI,
              validarCampo(voto.get(C_CODIGO_ELECCION)),
              validarCampo(voto.get(C_CODIGO_UBIGEO)),
              validarCampo(voto.get(C_CODIGO_OP)),
              validarCampo(voto.get(C_NOMBRE_OP)),
              validarCampo(voto.get(C_CODIGO_AMBITO_ELECTORAL)),
              validarCampo(voto.get("c_codigo_distrito_electoral")),
              validarCampo(voto.get("c_nombre_distrito_electoral")),
              validarCampo(voto.get("n_posicion_op")),
              validarCampo(voto.get("n_votos")),
              validarCampo(voto.get("n_porcentaje_votos_validos")),
              validarCampo(voto.get("n_porcentaje_votos_emitidos")),
              validarCampo(voto.get("n_votos_si")),
              validarCampo(voto.get("n_votos_no")),
              validarCampo(voto.get("n_votos_blanco")),
              validarCampo(voto.get("n_votos_nulo")),
              validarCampo(voto.get("n_votos_impugnado")),
              validarCampo(voto.get("n_electores_habiles"))
      )).append("\n")
    );
    createTextFile(nombreTxt, txtVotos);
  }

  private void createDataVotosCifras(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtVotos = new StringBuilder();

    txtVotos.append(IDTIPOELECCION).append(DELIMI).append(TXUBIGEO).append(DELIMI)
            .append(IDORGPOLITICA).append(DELIMI).append(TXORGPOLITICA).append(DELIMI)
            .append("IDDISTRITOELECTORAL").append(DELIMI).append("TXDISTRITOELECTORAL").append(DELIMI)
            .append(IDODPE).append(DELIMI).append("NUVOTOS").append(DELIMI)
            .append("NUVOTOSBL").append(DELIMI).append("NUVOTOSNU").append(DELIMI)
            .append("POVOTOSVALIDOS").append(DELIMI).append("POVOTOSEMITIDOS").append(DELIMI)
            .append("NUCOCIENTE").append(DELIMI).append("NUCIFRAREPARTIDORA").append(DELIMI)
            .append("NUREGIDORASIGNADO").append(DELIMI).append("TXOBSERVACION").append(DELIMI)
            .append("FGTIPODISTRIBUCION").append(DELIMI).append("NUVOTOSSI").append(DELIMI)
            .append("NUVOTOSNO").append(DELIMI).append("NUVOTOSIMP")
            .append("\n");

    lista.forEach(voto -> 
      txtVotos.append(validarCampo(voto.get(C_CODIGO_ELECCION))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_UBIGEO))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_OP))).append(DELIMI)
              .append(validarCampo(voto.get(C_NOMBRE_OP))).append(DELIMI)
              .append(validarCampo(voto.get("c_codigo_distrito_electoral"))).append(DELIMI)
              .append(validarCampo(voto.get("c_nombre_distrito_electoral"))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_AMBITO_ELECTORAL))).append(DELIMI)
              .append(validarCampo(voto.get("n_votos"))).append(DELIMI)
              .append(validarCampo(voto.get("n_votos_blanco"))).append(DELIMI)
              .append(validarCampo(voto.get("n_votos_nulo"))).append(DELIMI)
              .append(validarCampo(voto.get("n_porcentaje_votos_validos"))).append(DELIMI)
              .append(validarCampo(voto.get("n_porcentaje_votos_emitidos"))).append(DELIMI)
              .append(validarCampo(voto.get("n_cociente"))).append(DELIMI)
              .append(validarCampo(voto.get("n_cifra_repartidora"))).append(DELIMI)
              .append(validarCampo(voto.get("n_regidor_asignado"))).append(DELIMI)
              .append(validarCampo(voto.get("c_observacion"))).append(DELIMI)
              .append(validarCampo(voto.get("c_tipo_distribucion"))).append(DELIMI)
              .append(validarCampo(voto.get("n_votos_si"))).append(DELIMI)
              .append(validarCampo(voto.get("n_votos_no"))).append(DELIMI)
              .append(validarCampo(voto.get("n_votos_impugnado")))
              .append("\n")
    );
    createTextFile(nombreTxt, txtVotos);
  }

  private void createDataTablaActas(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtTablaActas = new StringBuilder();

    txtTablaActas.append(IDTIPOELECCION).append(DELIMI).append(TXUBIGEO).append(DELIMI)
            .append("NUELECTORESHABILES").append(DELIMI).append("NUCIUDADANOSVOTARON").append(DELIMI)
            .append("NUTOTALMESAS").append(DELIMI).append("NUMESASINSTALADAS").append(DELIMI)
            .append("NUMESASNOINSTALADAS").append(DELIMI).append("NUACTASCOMPUTADAS").append(DELIMI)
            .append("NUACTASCONTNORMALES").append(DELIMI).append("NUACTASANULADAS").append(DELIMI)
            .append("NUACTASEXTRAVIADAS").append(DELIMI).append(IDODPE).append(DELIMI)
            .append("NUACTASSINIESTRADAS")
            .append("\n");

    lista.forEach(voto -> 
      txtTablaActas.append(validarCampo(voto.get(C_CODIGO_ELECCION))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_UBIGEO))).append(DELIMI)
              .append(validarCampo(voto.get("n_electores_habiles"))).append(DELIMI)
              .append(validarCampo(voto.get("n_ciudadanos_votaron"))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_mesas"))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_mesas_instaladas"))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_mesas_no_instaladas"))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_actas_contabilizadas"))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_actas_contabilizadas_normales"))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_actas_anuladas"))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_actas_extraviadas"))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_AMBITO_ELECTORAL))).append(DELIMI)
              .append(validarCampo(voto.get("n_total_actas_siniestradas")))
              .append("\n")
    );
    createTextFile(nombreTxt, txtTablaActas);
  }

  private void createDataMesasNoInstaladas(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtMesas = new StringBuilder();

    txtMesas.append("NUMESA").append(DELIMI)
            .append(TXUBIGEO).append(DELIMI)
            .append("TXUBICACION").append(DELIMI)
            .append("TXCENTROVOTACION")
            .append("\n");

    lista.forEach(voto -> 
      txtMesas.append(validarCampo(voto.get("c_numero_mesa"))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_UBIGEO))).append(DELIMI)
              .append(validarCampo(voto.get("c_ubicacion"))).append(DELIMI)
              .append(validarCampo(voto.get("c_local_votacion")))
              .append("\n")
    );
    createTextFile(nombreTxt, txtMesas);
  }

  private void createDataMaestroOrganizacion(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtMaOrg = new StringBuilder();

    txtMaOrg.append("IDTIPOORGPOLITICA").append(DELIMI)
            .append("TXTIPOORGPOLITICA").append(DELIMI)
            .append(IDORGPOLITICA).append(DELIMI)
            .append(TXORGPOLITICA).append(DELIMI)
            .append(TXUBIGEO)
            .append("\n");

    lista.forEach(voto -> 
      txtMaOrg.append(validarCampo(voto.get("c_codigo_tipo_op"))).append(DELIMI)
              .append(validarCampo(voto.get("c_descripcion_tipo_op"))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_OP))).append(DELIMI)
              .append(validarCampo(voto.get(C_NOMBRE_OP))).append(DELIMI)
              .append(validarCampo(voto.get("c_codigo_ubigeo_op")))
              .append("\n")
    );
    createTextFile(nombreTxt, txtMaOrg);
  }

  private void createDataOrdenOrganizacion(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtOrden = new StringBuilder();

    txtOrden.append(TXUBIGEO).append(DELIMI)
            .append("NUORDEN").append(DELIMI)
            .append(IDORGPOLITICA).append(DELIMI)
            .append(IDTIPOELECCION)
            .append("\n");

    lista.forEach(voto -> 
      txtOrden.append(validarCampo(voto.get(C_CODIGO_UBIGEO))).append(DELIMI)
              .append(validarCampo(voto.get("n_posicion_op"))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_OP))).append(DELIMI)
              .append(validarCampo(voto.get(C_CODIGO_ELECCION)))
              .append("\n")
    );
    createTextFile(nombreTxt, txtOrden);
  }

  private void createDataTipoEleccion(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtTipoEleccion = new StringBuilder();

    txtTipoEleccion.append(IDTIPOELECCION).append(DELIMI)
            .append("TXTIPOELECCION")
            .append("\n");

    lista.forEach(voto -> 
      txtTipoEleccion.append(validarCampo(voto.get(C_CODIGO_ELECCION))).append(DELIMI)
              .append(validarCampo(voto.get("c_nombre_eleccion")))
              .append("\n")
    );
    createTextFile(nombreTxt, txtTipoEleccion);
  }

  private void createDataMaestroUbigeo(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtUbigeo = new StringBuilder();

    txtUbigeo.append(TXUBIGEO).append(DELIMI)
            .append("TXDEPARTAMENTO").append(DELIMI)
            .append("TXPROVINCIA").append(DELIMI)
            .append("TXDISTRITO")
            .append("\n");

    lista.forEach(voto -> 
      txtUbigeo.append(validarCampo(voto.get(C_CODIGO_UBIGEO))).append(DELIMI)
              .append(validarCampo(voto.get("c_nombre_ubigeo_nivel_01"))).append(DELIMI)
              .append(validarCampo(voto.get("c_nombre_ubigeo_nivel_02"))).append(DELIMI)
              .append(validarCampo(voto.get("c_nombre_ubigeo_nivel_03")))
              .append("\n")
    );
    createTextFile(nombreTxt, txtUbigeo);
  }

  private void createDataOdpe(final List<Map<String, Object>> lista, final String nombreTxt) throws IOException {

    StringBuilder txtMesas = new StringBuilder();

    txtMesas.append(IDODPE).append(DELIMI)
            .append("TXODPE")
            .append("\n");

    lista.forEach(voto -> 
      txtMesas.append(validarCampo(voto.get(C_CODIGO_AMBITO_ELECTORAL))).append(DELIMI)
              .append(validarCampo(voto.get("c_nombre_ambito_electoral")))
              .append("\n")
    );
    createTextFile(nombreTxt, txtMesas);
  }

  public void createTextFile(String filePath, StringBuilder content) throws IOException {
    try (OutputStream os = new FileOutputStream(filePath)) {
      os.write(content.toString().getBytes(StandardCharsets.UTF_8));
    }
  }

  private File createZipFile(List<File> filesToZip, String nameZip) throws GenericException, IOException {
    File zipFile = new File(nameZip);
    try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
      List<File> fileNew = filesToZip.stream().distinct().toList();
      for (File file : fileNew) {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);
        byte[] bytes = FileUtils.readFileToByteArray(file);
        zos.write(bytes, 0, bytes.length);
        zos.closeEntry();
      }
    }
    return zipFile;
  }

  public static byte[] convertFileToByteArray(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = fis.read(buffer)) != -1) {
        baos.write(buffer, 0, bytesRead);
      }

      return baos.toByteArray();
    }
  }

  private AnexoListaActaObservadaDTO mapListaObservadas(Map<String, Object> a) {
    AnexoListaActaObservadaDTO anexo = new AnexoListaActaObservadaDTO();
    anexo.setTipoeleccion(validarCampo(a.get("tipoeleccion")));
    anexo.setCodigoambito(validarCampo(a.get("codigoambito")));
    anexo.setCentrocomputo(validarCampo(a.get("centrocomputo")));
    anexo.setNumerolote(validarCampo(a.get("numerolote")));
    anexo.setUbigeo(validarCampo(a.get("ubigeo")));
    String idActa = validarCampo(a.get("acta"));
    anexo.setActa(idActa);
    anexo.setNumerocopia(validarCampo(a.get("numerocopia")));
    anexo.setEstadoacta(validarCampo(a.get("estadoacta")));
    anexo.setEstadoerrormaterial(validarCampo(a.get("estadoerrormaterial")));
    return anexo;
  }

  private AnexoTiposDTO mapTipoObservacion(Map<String, Object> a) {
    AnexoTiposDTO anexo = new AnexoTiposDTO();
    anexo.setCodigo(validarCampo(a.get("c_codigo_tipo_observacion")));
    anexo.setNombre(validarCampo(a.get("c_nombre_tipo_observacion")));
    return anexo;
  }

  private AnexoTiposDTO mapTipoErrorMaterial(Map<String, Object> a) {
    AnexoTiposDTO anexo = new AnexoTiposDTO();
    anexo.setCodigo(validarCampo(a.get("c_codigo_error_material")));
    anexo.setNombre(validarCampo(a.get("c_nombre_error_material")));
    return anexo;
  }

  private String validarCampo(Object valor) {
    return valor == null ? "" : valor.toString();
  }
}
