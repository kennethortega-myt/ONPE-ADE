package pe.gob.onpe.sceorcbackend.utils;

public enum TipoFiltro {
  NORMAL(1),
  NOINSTALADA(2),
  EXTRAVIADASI(3);

  private final int value;

  TipoFiltro(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
