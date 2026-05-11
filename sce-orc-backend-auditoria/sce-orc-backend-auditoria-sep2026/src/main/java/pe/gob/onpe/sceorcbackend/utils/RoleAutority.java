package pe.gob.onpe.sceorcbackend.utils;

public final class RoleAutority {

    private RoleAutority() {}

    public static final String ADMINISTRADOR_CC =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "')";

    public static final String ROLES_SCE_WEB =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "','" +
                    ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION + "','" +
                    ConstantesComunes.PERFIL_USUARIO_VERIFICADOR + "')";

    public static final String ROLES_SCE_WEB_MAS_REPORTES =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "','" +
                    ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION + "','" +
                    ConstantesComunes.PERFIL_USUARIO_REPORTES + "','" +
                    ConstantesComunes.PERFIL_USUARIO_VERIFICADOR + "')";

    public static final String SCE_SCANNER =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "','" +
                    ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER + "')";

    public static final String VERIFICADOR =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "','" +
                    ConstantesComunes.PERFIL_USUARIO_VERIFICADOR + "')";

    public static final String BACKUP_RESTORE =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_SUPER_ADMINISTRADOR + "','" +
                    ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "')";

    public static final String ROLES_SCE_REPORTES =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "','" +
                    ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION + "','" +
                    ConstantesComunes.PERFIL_USUARIO_VERIFICADOR + "','" +
                    ConstantesComunes.PERFIL_USUARIO_REPORTES + "')";

    public static final String CONTROL_DIGITAL =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "','" +
                    ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION + "')";

    public static final String ACCESO_TOTAL =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC + "','" +
                    ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION + "','" +
                    ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER + "','" +
                    ConstantesComunes.PERFIL_USUARIO_VERIFICADOR + "','" +
                    ConstantesComunes.PERFIL_USUARIO_REPORTES + "')";


    public static final String ONLY_SCE_SCANNER =
            "hasAnyAuthority('" + ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER + "')";

}
