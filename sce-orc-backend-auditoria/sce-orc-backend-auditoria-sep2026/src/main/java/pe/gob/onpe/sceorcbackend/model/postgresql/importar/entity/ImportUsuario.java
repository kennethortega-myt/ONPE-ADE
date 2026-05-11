package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;



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
public class ImportUsuario implements Serializable {

	private static final long serialVersionUID = 6523202473296363386L;

	@Id
    @Column(name = "n_usuario_pk")
    private Long id;

    @Column(name = "c_usuario")
    private String usuario;
    
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
    @Setter(AccessLevel.NONE)
    private Date	fechaModificacion;
    
    // nuevos
    
    @Column(name = "n_id_usuario")
    private Integer	idUsuario;

    @Column(name = "c_acronimo_proceso_electoral")
    private String	acronimoProceso;

    @Column(name = "c_nombre_centro_computo")
    private String	nombreCentroComputo;

    @Column(name = "c_codigo_uno")
    private String	codigo1;

    @Column(name = "n_id_perfil")
    private Integer	idPerfil;

    @Column(name = "n_codigo_dos")
    private Integer	codigo2;

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
    private Integer	personaAsignada;
    
    @PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
}
