package pe.gob.onpe.sceorcbackend.security.enums;

public enum Scopes {
    REFRESH_TOKEN;

    public String authority() {
        return "ROLE_" + this.name();
    }
}
