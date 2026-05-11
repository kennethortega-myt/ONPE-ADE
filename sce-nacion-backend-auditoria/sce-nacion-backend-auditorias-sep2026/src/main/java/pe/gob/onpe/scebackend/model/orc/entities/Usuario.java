package pe.gob.onpe.scebackend.model.orc.entities;

import lombok.*;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_usuario")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_usuario")
    @SequenceGenerator(name = "generator_usuario", sequenceName = "seq_tab_usuario_pk", allocationSize = 1)
    @Column(name = "n_usuario_pk")
    private Long id;

    @Column(name = "c_usuario")
    private String  usuario;
    
    @Column(name = "n_tipo_documento_identidad")
    private Integer tipoDocumentoIdentidad;
    
    @Column(name = "c_documento_identidad")
    private String documentoIdentidad;

    @Column(name = "c_perfil")
    private String perfil;

    @Column(name = "c_centro_computo")
    private String centroComputo;

    @Column(name = "n_sesion_activa")
    private Integer sesionActiva;

    @Column(name = "n_actas_asignadas")
    private Integer actasAsignadas;

    @Column(name = "n_actas_atendidas")
    private Integer actasAtendidas;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String 	usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String	usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date	fechaModificacion;

    @Column(name = "n_id_usuario")
    private Integer	idUsuario;

    @Column(name = "c_acronimo_proceso_electoral")
    private String	acronimoProceso;

    @Column(name = "c_nombre_centro_computo")
    private String	nombreCentroComputo;

    @Column(name = "c_codigo_uno")
    private String	clave;

    @Column(name = "n_id_perfil")
    private Integer	idPerfil;

    @Column(name = "n_codigo_dos")
    private Integer	claveTemporal;

    @Column(name = "c_nombres")
    private String	nombres;
    
    @Column(name = "c_correo")
    private String	correos;
    
    @Column(name = "b_desincronizado_sasa")
    private boolean desincronizadoSasa;

    @Column(name = "c_apellido_paterno")
    private String	apellidoPaterno;

    @Column(name = "c_apellido_materno")
    private String	apellidoMaterno;

    @Column(name = "n_persona_asignada")
    private Integer personaAsignada;

}
