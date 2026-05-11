package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.Optional;

public interface SasaTokenService {

    public void addToken(Integer usuarioId, String token);

    public Optional<String> getToken(Integer usuarioId);

    public void deleteToken(Integer usuarioId);

}
