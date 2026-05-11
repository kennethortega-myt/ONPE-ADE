package pe.gob.onpe.scebackend.model.service;

public interface IMessageService {

  String getMessage(String key);

  String getMessage(String key, String idioma);

}
