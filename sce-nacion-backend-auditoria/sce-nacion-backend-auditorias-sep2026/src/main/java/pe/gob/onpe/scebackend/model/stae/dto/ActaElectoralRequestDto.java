package pe.gob.onpe.scebackend.model.stae.dto;

import java.util.List;

import lombok.Data;

@Data
public class ActaElectoralRequestDto {

	private String numeroActa;
	private Integer flagNulidad;
    private String numeroCopiaInstalacion;
    private String numeroCopiaSufragio;
    private String numeroCopiaEscrutinio;
    private String rutaArchivoInstalacion;
    private String rutaArchivoSufragio;
    private String rutaArchivoEscrutinio;
    private Integer eleccion;
    private Integer cvas;
    private String estadoActa;
    private String estadoActaResolucion;
    private String estadoCompu;
    private String estadoErrorAritmetico;
    private String fechaInstalacion;
    private String horaInstalacion;
    private String horaSufragio;
    private String fechaEscrutinio;
    private String horaEscrutinio;
    private String observacionesInstalacion;
    private String observacionesSufragio;
    private String observacionesEscrutinio;
    private Integer totalVotos;
    private Integer votosCalculados;

    private List<DetalleActaDto> detalleActa;
    private List<MiembroMesaDto> detalleMMInstalacion;
    private List<MiembroMesaDto> detalleMMSufragio;
    private List<MiembroMesaDto> detalleMMEscrutinio;
    private List<AsistenciaDto> detalleAsistenciaMM;
    private List<NoSorteadoDto> detalleNoSorteados;
    private List<PersoneroDto> detallePersoneros;
    
	
}
