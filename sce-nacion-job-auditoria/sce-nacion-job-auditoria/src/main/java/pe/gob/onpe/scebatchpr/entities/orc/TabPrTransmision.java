package pe.gob.onpe.scebatchpr.entities.orc;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "tab_pr_transmision")
public class TabPrTransmision implements Serializable {

	private static final long serialVersionUID = -4754759996346026037L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_pr_transmision_pk")
    private Long id;

    @Column(name = "n_acta")
    private Long idActa;

    @Column(name = "c_nombre_vista")
    private String nombreVista;

    @Column(name = "c_trama", columnDefinition = "text")
    private String trama;
    
    @Column(name = "c_correlativo",nullable = true)
    private String correlativo;
    
    @Column(name = "c_mensaje",nullable = true)
    private String mensaje;

    @Column(name = "n_estado", nullable = false)
    private Integer estado;
    
    @Column(name = "n_enviado", nullable = false)
    private Integer enviado;

    @Column(name = "n_activo", nullable = false)
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion", nullable = false)
    private String audUsuarioCreacion;

    @Column(name = "d_aud_fecha_creacion", nullable = false)
    private Date audFechaCreacion;

    @Column(name = "c_aud_usuario_transmision")
    private String audUsuarioTransmision;

    @Column(name = "d_aud_fecha_transimision")
    private Date audFechaTransmision;
	
}