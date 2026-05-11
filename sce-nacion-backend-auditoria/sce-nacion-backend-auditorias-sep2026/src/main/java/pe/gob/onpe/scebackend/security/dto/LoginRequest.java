package pe.gob.onpe.scebackend.security.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

  private String username;

  private String password;

  private String dni;

  private String captcha;

  private Boolean bot;

  private Boolean botok;

  private String message;

  private Map<String, Object> adicional;

  @JsonCreator
  public LoginRequest(@JsonProperty("username") String username,
      @JsonProperty("password") String password,
      @JsonProperty("dni") String dni,
      @JsonProperty("captcha") String captcha
  ) {
    this.username = username;
    this.password = password;
    this.dni = dni;
    this.captcha = captcha;
    this.bot = false;
    this.botok = false;
    this.message = null;
  }

  public LoginRequest() {
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getDni() {
    return dni;
  }

  public String getCaptcha() {
    return captcha;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public Boolean getBot() {
    return bot;
  }

  public void setBot(Boolean bot) {
    this.bot = bot;
  }

  public Boolean getBotok() {
    return botok;
  }

  public void setBotok(Boolean botok) {
    this.botok = botok;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, Object> getAdicional() {
    if (adicional == null) {
      adicional = new HashMap<>();
    }
    return adicional;
  }

  public void setAdicional(Map<String, Object> adicional) {
    this.adicional = adicional;
  }
}