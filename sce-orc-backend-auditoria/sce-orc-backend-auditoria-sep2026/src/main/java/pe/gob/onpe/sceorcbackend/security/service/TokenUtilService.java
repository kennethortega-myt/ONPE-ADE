package pe.gob.onpe.sceorcbackend.security.service;

import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;

public interface TokenUtilService {
    TokenInfo getInfo(String authorization);
    String getTimeExpired(String authorization);
    long getTimeExpiredSeconds(String authorization);
}
