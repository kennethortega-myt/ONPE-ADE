package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service;

import java.util.concurrent.CompletableFuture;

public interface ImportarPadronBdAsyncService {

    CompletableFuture<Void> migrar(String cc, String usuario, String proceso);

}
