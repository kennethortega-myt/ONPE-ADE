package pe.gob.onpe.sceorcbackend.security;

public interface TokenExtractor {
    String extract(String payload);
}
