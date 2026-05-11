package pe.gob.onpe.sceorcbackend.security.dto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UserContext {
    private final String username;
    private String usernameCierreCC;
    private String fechaCierreCC;
    private String usernameSinEncriptar;
    private final String name;
    private String validator;
    private Integer userId;
    private String odpe;
    private String local;
    private Integer centroAcopio;
    private Integer perfilId;
    private String acronimoProceso;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
    private Integer claveNueva;
    private Integer estadoCentroComputo;
    private String idSession;
    private final List<GrantedAuthority> authorities;

    private UserContext(String username, String name, List<GrantedAuthority> authorities) {
        this.username = username;
        this.name = name;
        this.authorities = authorities;
    }

    private UserContext(String username, String name, String validator, List<GrantedAuthority> authorities) {
        this.username = username;
        this.name = name;
        this.validator = validator;
        this.authorities = authorities;
    }

    public static UserContext create(String username, String name, List<GrantedAuthority> authorities) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("Username is blank: " + username);
        return new UserContext(username, name, authorities);
    }

    public static UserContext create(String username, String name, String validator, List<GrantedAuthority> authorities) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("Username is blank: " + username);
        return new UserContext(username, name, validator, authorities);
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOdpe() {
        return odpe;
    }

    public void setOdpe(String odpe) {
        this.odpe = odpe;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getPerfilId() {
        return perfilId;
    }

    public void setPerfilId(Integer perfilId) {
        this.perfilId = perfilId;
    }

    public String getValidator() {
        return validator;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Integer getCentroAcopio() {
        return centroAcopio;
    }

    public void setCentroAcopio(Integer centroAcopio) {
        this.centroAcopio = centroAcopio;
    }

    public String getAcronimoProceso() {
        return acronimoProceso;
    }

    public void setAcronimoProceso(String acronimoProceso) {
        this.acronimoProceso = acronimoProceso;
    }

    public String getCodigoCentroComputo() {
        return codigoCentroComputo;
    }

    public void setCodigoCentroComputo(String codigoCentroComputo) {
        this.codigoCentroComputo = codigoCentroComputo;
    }

    public String getNombreCentroComputo() {
        return nombreCentroComputo;
    }

    public void setNombreCentroComputo(String nombreCentroComputo) {
        this.nombreCentroComputo = nombreCentroComputo;
    }

    public String getUsernameSinEncriptar() {
        return usernameSinEncriptar;
    }

    public void setUsernameSinEncriptar(String usernameSinEncriptar) {
        this.usernameSinEncriptar = usernameSinEncriptar;
    }

    public Integer getClaveNueva() {
        return claveNueva;
    }

    public void setClaveNueva(Integer claveNueva) {
        this.claveNueva = claveNueva;
    }

    public Integer getEstadoCentroComputo() {
        return estadoCentroComputo;
    }

    public void setEstadoCentroComputo(Integer estadoCentroComputo) {
        this.estadoCentroComputo = estadoCentroComputo;
    }

    public String getUsernameCierreCC() {
        return usernameCierreCC;
    }

    public void setUsernameCierreCC(String usernameCierreCC) {
        this.usernameCierreCC = usernameCierreCC;
    }

    public String getFechaCierreCC() {
        return fechaCierreCC;
    }

    public void setFechaCierreCC(String fechaCierreCC) {
        this.fechaCierreCC = fechaCierreCC;
    }

	public String getIdSession() {
		return idSession;
	}

	public void setIdSession(String idSession) {
		this.idSession = idSession;
	}
    
    
}
