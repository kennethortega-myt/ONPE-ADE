package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class CatResumenTotalPorCentroComputo {
    private String cCentCompu; // código centro de cómputo
    private String cDescCompu;  // Descripción centro de cómputo
    private String habilitado;  //estado Habilitado para Cierre  SI, NO
    private String cTipoConexion; // tipo de conexión todos, cobre, satelital
    private String cTipoCc;  // tipo de centro de cómputo T1,T2,T3 ....
    private double mesasInstal; // Total Mesas Instaladas
    private double mesasNoInstal; // Total Mesas No Instaladas
    private double totActasExtravPr; //Actas Extraviadas por resulución
    private double totActasSiniestPr; // Actas Siniestradas por resulución
    private double actasProcesadaPr; //Actas Procesadas (Recibidas)
    private double actasCompuPr; // Actas Contabilizadas Por resulución
    private double actasDigitPr;// Actas digitalizadas  (digitalización->actas)
    private double resolIngresadas; //actas digitalizadas con resulución ingresados al sistema (digitalización->Resolución->Ingr.Sist.)
    private double resolDigitalizadas; //actas digitalizadas con resulución avance digitalizado (digitalización->Resolución->Avanc.Digit.)
    private double actasTransPr;  //trasmisión de imágenes Actas
    private double resolTransmitidas;
    private double omisosVotacion; // Registro de Omisos->Votantes
    private double omisosMmesa; //Registro de Omisos --> Miembros  (omisos miembros de mesa)
    private double contablePr;
    private double digitalizacionPr;
    private double digiResolPr;
    private double transmisionPr;
    private double tranResolPr;
    private double votantes;
    private double miembros;
    private double totalActas;
    private double actasDigitCalidad; // Control de Calidad ->Actas
    private double resolDigitCalidad; // Control de Calidad ->Resol
    private double digitalizacionCalidad;
    private double digiResolCalidad;
    private double nElecHabil;
    private double mesasAInstalar; // Total de mesas a instalar

    /**/
    private double totActasExtravPrComodin; // esta atributo se crea para comodin en el reporte  RPT040105.jrxml
    private double totActasSiniestPrComodin;// esta atributo se crea para comodin en el reporte  RPT040105.jrxml
}
