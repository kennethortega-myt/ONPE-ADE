package pe.gob.onpe.scebackend.model.orc.entities;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tab_refresh_token")
public class TabRefreshToken implements Serializable {

	private static final long serialVersionUID = -8237336786613640408L;

	@Id
	@Column(name = "n_refresh_token_pk")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "c_usuario")
    private String usuario;

	@Column(name = "d_expiry_date")
    private Instant expiryDate;

	@Column(name = "c_token")
    private String token;

	@Column(name = "n_activo")
    private Integer activo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_usuario", referencedColumnName = "n_usuario_pk", nullable = false)
    private Usuario userInfo;

    @Column(name = "c_aud_usuario_creacion")
    private String audUsuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date audFechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String audUsuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date audFechaModificacion;


}
