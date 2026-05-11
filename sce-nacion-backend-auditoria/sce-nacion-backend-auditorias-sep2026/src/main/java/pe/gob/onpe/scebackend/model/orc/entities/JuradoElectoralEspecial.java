package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tab_jurado_electoral_especial")
public class JuradoElectoralEspecial implements Serializable {

	private static final long serialVersionUID = -8046569293132717441L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tab_jurado_electoral_especial_pk")
    @SequenceGenerator(name = "seq_tab_jurado_electoral_especial_pk", sequenceName = "seq_tab_jurado_electoral_especial_pk", allocationSize = 1)
    @Column(name = "n_jurado_electoral_especial_pk")
    private Integer id;

    @Column(name = "c_codigo_centro_computo")
    private String codigoCentroComputo;
    
    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "c_id_jee")
    private String idJee;

    @Column(name = "c_direccion")
    private String direccion;

    @Column(name = "c_apellido_paterno_representante")
    private String apellidoPaternoRepresentante;

    @Column(name = "c_apellido_materno_representante")
    private String apellidoMaternoRepresentante;

    @Column(name = "c_nombres_representante")
    private String nombresRepresentante;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;
	
}
