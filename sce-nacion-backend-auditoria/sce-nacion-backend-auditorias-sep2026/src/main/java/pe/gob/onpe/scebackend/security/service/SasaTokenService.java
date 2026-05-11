package pe.gob.onpe.scebackend.security.service;

import java.util.Optional;

public interface SasaTokenService {

    public void addToken(Integer usuarioId, String token);

    public Optional<String> getToken(Integer usuarioId);

    public void deleteToken(Integer usuarioId);

}
