package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteResultadoActasContDto {

	private String codAgrupacion;
	private String desAgrupacion;
	private Integer numVotos;
	private String codUbigeo;
	private String departamento;
	private String provincia;
	private String distrito;
	private String codOdpe;
	private String descCompu;
	private String descOdpe;
	private String codCompu;
	private Integer electoresHabiles;
	private Integer totalCiudadVotaron;
	private Integer ainstalar;
	private Integer porProcesar;
	private Integer contabNormal;
	private Integer contabInpugnadas;
	private String errorMaterial;
	private String ilegible;
	private String incompleta;
	private String solicitudNulidad;
	private String sinDatos;
	private String actExt;
	private String sinFirma;
	private String otrasObserv;
	private Integer contabAnuladas;
	private Integer mesasNoInstaladas;
	private Integer mesasInstaladas;
	private Integer actasProcesadas;
	private String actSin;
    private Boolean esAgrupacionPolitica;
    private Integer pendiente;

    //******* VOTOS CPR ************
	private Integer votosSI;
	private Integer votosNO;
	private Integer votosBL;
	private Integer votosNL;
	private Integer calculo;
	//******* VOTOS PREFERENCIALES *******
	private Integer totalVotos;
	private Integer numVotos1;
	private Integer numVotos2;
	private Integer numVotos3;
	private Integer numVotos4;
	private Integer numVotos5;
	private Integer numVotos6;
	private Integer numVotos7;
	private Integer numVotos8;
	private Integer numVotos9;
	private Integer numVotos10;
	private Integer numVotos11;
	private Integer numVotos12;
	private Integer numVotos13;
	private Integer numVotos14;
	private Integer numVotos15;
	private Integer numVotos16;
	private Integer numVotos17;
	private Integer numVotos18;
	private Integer numVotos19;
	private Integer numVotos20;
	private Integer numVotos21;
	private Integer numVotos22;
	private Integer numVotos23;
	private Integer numVotos24;
	private Integer numVotos25;
	private Integer numVotos26;
	private Integer numVotos27;
	private Integer numVotos28;
	private Integer numVotos29;
	private Integer numVotos30;
	private Integer numVotos31;
	private Integer numVotos32;
	private Integer numVotos33;
	private Integer numVotos34;
	private Integer numVotos35;
	private Integer numVotos36;
	
}
