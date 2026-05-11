package pe.gob.onpe.scebatchpr.entities.admin;


import lombok.Data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@Table(name = "tab_configuracion_proceso_electoral", schema = "sce_admin")
public class ConfiguracionProcesoElectoral implements Serializable {

	private static final long serialVersionUID = -100247436880641987L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_configuracion_proceso_electoral")
    @SequenceGenerator(name = "generator_tab_configuracion_proceso_electoral", sequenceName = "seq_tab_configuracion_proceso_electoral_pk", allocationSize = 1)
    @Column(name = "n_configuracion_proceso_electoral_pk")
    private Integer id;

    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "c_acronimo")
    private String acronimo;

    @Column(name = "c_nombre_dblink")
    private String nombreDbLink;
    
    @Column(name = "c_nombre_esquema_principal")
    private String nombreEsquemaPrincipal;

    @Column(name = "c_nombre_esquema_bdonpe")
    private String nombreEsquemaBdOnpe;

    @Column(name = "d_fecha_convocatoria")
    private Date fechaConvocatoria;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "n_vigente")
    private Integer vigente;
    
    @Column(name = "n_etapa")
    private Integer etapa;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;

}
