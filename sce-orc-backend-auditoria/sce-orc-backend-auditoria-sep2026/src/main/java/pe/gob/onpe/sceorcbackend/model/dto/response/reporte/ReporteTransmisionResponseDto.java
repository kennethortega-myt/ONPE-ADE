package pe.gob.onpe.sceorcbackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteTransmisionResponseDto {

	private Integer pr;
	private Integer cn;
	private Integer pa;
	private Integer total;
	private Integer re;
	private Integer ia;
	private Integer ir;
	private String fe;
	private Integer pc;
	private String descCompu;
	private Integer co;
	private Integer rg;
	private Integer pv;
	private Integer totalDist;
	private Integer totalProv;
	private Integer totalCons;
	private Integer totalReg;
	private Integer rele;
	private Integer rha;
	private Integer rper;
	private Integer rme;

	private Integer transPresidente;
	private Integer transParlamentoAndino;
	private Integer transDiputados;
	private Integer transSenDistMultiple;
	private Integer transSenDistUnico;


	private Integer pendPresidente;
	private Integer pendParlamentoAndino;
	private Integer pendDiputados;
	private Integer pendSenDistMultiple;
	private Integer pendSenDistUnico;
}