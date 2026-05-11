package pe.gob.onpe.scebackend.security.service;

import pe.gob.onpe.scebackend.security.dto.TokenInfo;

public interface TokenUtilService {
    TokenInfo getInfo(String authorization);
    String getTimeExpired(String authorization);
    long getTimeExpiredSeconds(String authorization);
}
