package pe.gob.onpe.scebackend.utils;

public final class RoleAutority {

    private RoleAutority() {}

    public static final String ADMINISTRADOR_NAC =
            "hasAnyAuthority('" + SceConstantes.PERFIL_ADM_NAC + "')";

    public static final String ROLES_SCE_WEB =
            "hasAnyAuthority('" + SceConstantes.PERFIL_ADM_NAC + "','" +
                    SceConstantes.PERFIL_REPO_NAC + "')";

    public static final String ACCCESO_TOTAL =
            "hasAnyAuthority('" + SceConstantes.PERFIL_ADM_NAC + "','" +
                    SceConstantes.PERFIL_REPO_NAC + "','" +
                    SceConstantes.PERFIL_STAE + "')";

    public static final String SERVICE_EXTERNO =
            "hasAnyAuthority('" + SceConstantes.PERFIL_EXTRANJERO + "')";

    public static final String SERVICE_JNE =
            "hasAnyAuthority('" + SceConstantes.PERFIL_JNE + "')";
}
