package pe.gob.onpe.scebackend.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;


public class JwtAuthentication implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    @JsonIgnore
    private String username;
    @JsonIgnore
    private String password;
    private String rol;
    private String ip;
    private String mac;
    private String agente;
    private String browser;

    public JwtAuthentication() {
        super();
    }

    public JwtAuthentication(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAgente() {
        return agente;
    }

    public void setAgente(String agente) {
        this.agente = agente;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JwtAuthentication that = (JwtAuthentication) o;

        if (!getUsername().equals(that.getUsername())) return false;
        if (!getIp().equals(that.getIp())) return false;
        if (!getMac().equals(that.getMac())) return false;
        if (!getAgente().equals(that.getAgente())) return false;
        return getBrowser().equals(that.getBrowser());
    }

    @Override
    public String toString() {
        return "JwtAuthentication{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", rol='" + rol + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", agente='" + agente + '\'' +
                ", browser='" + browser + '\'' +
                '}';
    }
}
