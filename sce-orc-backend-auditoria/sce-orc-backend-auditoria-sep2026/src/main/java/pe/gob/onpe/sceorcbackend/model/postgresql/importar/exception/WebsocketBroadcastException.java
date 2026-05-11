package pe.gob.onpe.sceorcbackend.model.postgresql.importar.exception;

public class WebsocketBroadcastException extends RuntimeException {
   
	private static final long serialVersionUID = 1171807920566399274L;

	public WebsocketBroadcastException(String message, Throwable cause) {
        super(message, cause);
    }
}