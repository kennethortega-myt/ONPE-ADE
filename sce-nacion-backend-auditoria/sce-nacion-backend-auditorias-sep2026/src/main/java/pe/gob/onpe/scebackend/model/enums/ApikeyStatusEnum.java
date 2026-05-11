package pe.gob.onpe.scebackend.model.enums;

public enum ApikeyStatusEnum {
    INACTIVO(0),
    ACTIVO(1),
    BAJA(2);

    private int value;

   private ApikeyStatusEnum(final int value) {
        this.value = value;
    }
}
