package pe.gob.onpe.sceorcbackend.utils;

public class ConstantsQueues {

    private ConstantsQueues() {
    }

    public static final String EXCHAGE_DIRECT = "amq.direct";

    public static final String NAME_QUEUE_NEW_ACTA = "sce-queue-new-acta";
    public static final String ROUTING_KEY_NEW_ACTA = "sce-queue-new-acta-rt";
    
    public static final String NAME_QUEUE_NEW_ACTA_CELESTE = "sce-queue-new-acta-celeste";
    public static final String ROUTING_KEY_NEW_ACTA_CELESTE = "sce-queue-new-acta-celeste-rt";

    public static final String NAME_QUEUE_PROCESS_ACTA = "sce-queue-process-acta";
    public static final String ROUTING_KEY_PROCESS_ACTA = "sce-queue-process-acta-rt";

    public static final String NAME_QUEUE_PROCESS_LISTA_ELECTORES = "sce-queue-process-lista-electores";
    public static final String ROUTING_KEY_PROCESS_LISTA_ELECTORES = "sce-queue-process-lista-electores-rt";

    public static final String NAME_QUEUE_PROCESS_MIEBROS_MESA = "sce-queue-process-miembros_mesa";
    public static final String ROUTING_KEY_PROCESS_MIEMBROS_MESA = "sce-queue-process-miembros_mesa-rt";

    public static final String NAME_QUEUE_PROCESS_ACTA_STAE = "sce-queue-process-acta-stae";
    public static final String ROUTING_KEY_PROCESS_ACTA_STAE = "sce-queue-process-acta-stae-rt";
    
    public static final String NAME_QUEUE_PROCESS_TRANSMISION = "sce-queue-process-transmision";
    public static final String ROUTING_KEY_PROCESS_TRANSMISION = "sce-queue-process-transmision-rt";
    
    public static final String NAME_QUEUE_PROCESS_TRANSMISION_SEND = "sce-queue-process-transmision-envio";
    public static final String ROUTING_KEY_PROCESS_TRANSMISION_SEND = "sce-queue-process-transmision-envio-rt";

    public static final String NAME_QUEUE_PROCESS_ACTA_PREDICTION = "sce-queue-process-acta-prediction";
    public static final String ROUTING_KEY_PROCESS_ACTA_PREDICTION = "sce-queue-process-acta-prediction-rt";


    public static final String SUFFIX_DLQ = "-dlq";
    public static final String SUFFIX_DLQ_RT = "-dlq-rt";
}
