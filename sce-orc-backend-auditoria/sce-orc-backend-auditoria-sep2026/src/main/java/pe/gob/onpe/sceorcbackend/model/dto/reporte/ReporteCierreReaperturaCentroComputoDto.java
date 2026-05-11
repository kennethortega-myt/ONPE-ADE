package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
@Setter
public class ReporteCierreReaperturaCentroComputoDto {
    private Integer nunmeroEleccion;
    private String codigoCentroComputo;
    private Integer numeroMesasInstalar;
    private Integer numeroActasProcesadas;
    private Integer nunmeroActasComputadas;
    private Integer numeroControlCalidadActas;
    private Integer numeroResoluciones;
    private Integer numeroOmisosElectores;
    private Integer numeroOmisosMm;
    private Integer nunmeroOmisosMe;
    private Integer numeroOmisosPer;
    private String  nombreEleccion;
    private String  nombreCentro;

    private String descOdpe;
    private String moTivo;
    private String dateHora;
    private String dateDia;
    private String dateMes;
    private String dateAcierre;

    public ReporteCierreReaperturaCentroComputoDto() {
        LocalDateTime now = LocalDateTime.now();

        this.dateHora = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.dateDia = String.valueOf(now.getDayOfMonth());
        this.dateMes = now.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "PE")).toUpperCase();
        this.dateAcierre = String.valueOf(now.getYear()).substring(2);

        this.descOdpe = ConstantesComunes.OFICINA_DEFAULT;
        this.moTivo = ConstantesComunes.MOTIVO_DEFAULT;
    }

    public void setDescOdpeDinamico(String centroComputo) {
        if (centroComputo != null && !centroComputo.trim().isEmpty()) {
            this.descOdpe = centroComputo.toUpperCase();
        }
    }

}