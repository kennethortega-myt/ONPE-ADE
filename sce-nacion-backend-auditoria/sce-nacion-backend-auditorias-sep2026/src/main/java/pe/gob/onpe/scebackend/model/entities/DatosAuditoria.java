package pe.gob.onpe.scebackend.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class DatosAuditoria {

    @Column(name = "c_aud_usuario_creacion")
    protected String 	usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    protected Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    protected String	usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    protected Date	fechaModificacion;
}
