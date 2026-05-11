package pe.gob.onpe.scebackend.model.service.impl;

import java.util.Locale;

import pe.gob.onpe.scebackend.model.service.IMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class MessageService implements IMessageService {

  @Autowired
  private MessageSource messageSource;

  @Override
  public String getMessage(String key) {
    String message = "";
    try {
      message = messageSource.getMessage(key, null, new Locale("es"));
    } catch (Exception ex) {
      return "";
    }
    return message;
  }

  @Override
  public String getMessage(String key, String idioma) {
    String message = "";
    try {
      message = messageSource.getMessage(key, null, new Locale(idioma));
    } catch (Exception ex) {
      return "";
    }
    return message;
  }
}
