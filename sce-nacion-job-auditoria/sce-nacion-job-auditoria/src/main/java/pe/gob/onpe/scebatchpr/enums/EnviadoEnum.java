package pe.gob.onpe.scebatchpr.enums;

public enum EnviadoEnum {

	ENVIADO("Enviado", 1),
    SIN_ENVIAR("Sin Enviar", 0);
	
	private final String nombre;
    private final Integer valor;

    EnviadoEnum(String nombre, Integer valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getValor() {
        return valor;
    }
	
}
