package pe.gob.onpe.sceorcbackend.security.utils;

import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.gob.onpe.sceorcbackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.sceorcbackend.security.jwt.JwtConstant;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import jakarta.servlet.http.HttpServletRequest;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

public class Util {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());



    public JwtAuthentication infoPC(HttpServletRequest request) {
        try {
            JwtAuthentication jwt = new JwtAuthentication();

            setIpAddress(request, jwt);
            setUserAgent(request, jwt);
            setOperatingSystem(jwt);
            setBrowser(jwt);

            return jwt;
        } catch (Exception ex) {
            logger.error("Error in infoPC", ex);
            return null;
        }
    }


    private void setIpAddress(HttpServletRequest request, JwtAuthentication jwt) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null) {
            int ipAddressSeparator = ipAddress.indexOf(",");
            if (ipAddressSeparator >= 0) {
                ipAddress = ipAddress.substring(0, ipAddressSeparator);
                logger.info("ipAddress {}", ipAddress);
            }
            jwt.setIp("127.0.0.0"); // Consider using the actual ipAddress instead of hardcoding
            jwt.setMac("onpe.gob.pe");
        }
    }


    private void setUserAgent(HttpServletRequest request, JwtAuthentication jwt) {
        String userAgent = request.getHeader("User-Agent");
        jwt.setAgente(userAgent);
    }

    private void setOperatingSystem(JwtAuthentication jwt) {
        String userAgent = jwt.getAgente().toLowerCase();
        String os = determineOS(userAgent);
    }

    private String determineOS(String userAgent) {
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone")) return "iPhone";
        if (userAgent.contains("x11")) return "Unix";
        return "Desconocido";
    }

    private void setBrowser(JwtAuthentication jwt) {
        String userAgent = jwt.getAgente().toLowerCase();
        String browser = determineBrowser(userAgent, jwt.getAgente());
        jwt.setBrowser(browser);
    }

    private String determineBrowser(String user, String userAgent) {
        String browser="";

        if (user.contains("msie")) {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (user.contains("opr")) {
                browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
            }
        } else if (user.contains("chrome")) {
            browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {

            browser = "Netscape-?";
        } else if (user.contains("firefox")) {
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
        } else {
            browser = "Desconocido";
        }
        logger.info("JwtAuthentication -->  6 browser {}", browser);

        return browser;

    }



    public String encryJson(Object object) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return c.encrypt(new GsonBuilder().setDateFormat(SceConstantes.PATTERN_YYYY_MM_DD_HH12_MM_SS_DASH).create().toJson(object));
        } catch (Exception e) {
           logger.error("Error: {}",e.getMessage());
            return "";
        }
    }

    public JwtAuthentication dencryString(String validator) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return new GsonBuilder().setDateFormat(SceConstantes.PATTERN_YYYY_MM_DD_HH12_MM_SS_DASH).create().fromJson(c.decrypt(validator), JwtAuthentication.class);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR,e.getMessage());
            return null;
        }
    }


    public String encryStringB(String object) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return c.encrypt(object);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR,e.getMessage());
            return "";
        }
    }

    public String dencryStringB(String object) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return c.decrypt(object);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR,e.getMessage());
            return null;
        }
    }

}
