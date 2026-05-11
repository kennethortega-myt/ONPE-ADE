package pe.gob.onpe.scebackend.utils.constantes;

public class ConstantsQueues {

    private ConstantsQueues() {
        throw new UnsupportedOperationException("ConstantsQueues es una clase utilitaria y no puede ser instanciada");
    }
    public static final String EXCHAGE_DIRECT = "amq.direct";

    public static final String NAME_QUEUE_NEW_ACTA = "sce-queue-new-acta";
    public static final String ROUTING_KEY_NEW_ACTA = "new-acta";

    public static final String NAME_QUEUE_PROCESS_ACTA = "sce-queue-process-acta";
    public static final String ROUTING_KEY_PROCESS_ACTA = "process-acta";

    public static final String NAME_QUEUE_TRANSMISSION_ACTA = "sce-queue-transmission-acta";
    public static final String ROUTING_KEY_TRANSMISSION_ACTA = "transmission-acta";


    public static final String NAME_QUEUE_RESOLUCION = "sce-queue-transmision-resolucion";
    public static final String ROUTING_KEY_QUEUE_RESOLUCION = "routkey-sce-queue-transmision-resolucion";


    public static final String NAME_QUEUE_ENVIO_JEE = "sce-queue-transmision-envio-jee";
    public static final String ROUTING_KEY_QUEUE_ENVIO_JEE = "routkey-sce-queue-transmision-envio-jee";



}
