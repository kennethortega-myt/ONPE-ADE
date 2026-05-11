package pe.gob.onpe.scebackend.security;

public interface TokenExtractor {
    String extract(String payload);
}
