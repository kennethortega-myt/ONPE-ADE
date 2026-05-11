package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

public interface IMessageService {

  String getMessage(String key);

  String getMessage(String key, String idioma);

}
