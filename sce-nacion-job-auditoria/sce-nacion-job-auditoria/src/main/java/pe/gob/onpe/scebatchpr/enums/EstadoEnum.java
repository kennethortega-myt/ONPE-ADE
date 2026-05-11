package pe.gob.onpe.scebatchpr.enums;

public enum EstadoEnum {

	SIN_CONFIRMAR("Sin confirmar", 0),
    CONFIRMADO("Confirmado", 1),
	ENVIADO("Enviado", 2),
	NO_EXISTE("Confirimado (No existe el registro)", 3);

    private final String nombre;
    private final Integer valor;

    EstadoEnum(String nombre, Integer valor) {
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
