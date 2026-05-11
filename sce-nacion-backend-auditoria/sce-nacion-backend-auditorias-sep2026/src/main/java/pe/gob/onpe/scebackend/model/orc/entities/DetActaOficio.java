package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "det_acta_oficio")
public class DetActaOficio implements Serializable {

    private static final long serialVersionUID = 5108416028401510420L;

    @Id
    @Column(name = "n_det_acta_oficio_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_det_acta_oficio_pk")
    @SequenceGenerator(name = "seq_det_acta_oficio_pk", sequenceName = "seq_det_acta_oficio_pk", allocationSize = 1)
    private Integer id;

    @Column(name = "c_id_det_acta_oficio_cc")
    private String idCc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_oficio", referencedColumnName = "n_oficio_pk")
    private Oficio oficio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk")
    private Acta acta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_acta_celeste", referencedColumnName = "n_acta_celeste_pk")
    private ActaCeleste actaCeleste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_cab_acta_formato", referencedColumnName = "n_cab_acta_formato_pk")
    private CabActaFormato cabActaFormato;

    @Column(name = "c_numero_resolucion")
    private String numeroResolucionJne;

    @Column(name = "c_expediente")
    private String numeroExpediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_jne", referencedColumnName = "n_archivo_pk", nullable = true)
    private Archivo archivoJne;

    @Column(name = "c_observacion")
    private String observacion;

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