package pe.gob.onpe.scebackend.security.utils;

import com.google.gson.GsonBuilder;

import pe.gob.onpe.scebackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.scebackend.security.jwt.JwtConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Util {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public JwtAuthentication infoPC(HttpServletRequest request) {
        try {
            JwtAuthentication jwt = new JwtAuthentication();


            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();  // IP
            }

//            InetAddress addr = InetAddress.getByName(ipAddress);  // DOMAIN NAME from IP
//            String host = addr.getHostName();
//            jwt.setIp(ipAddress);
//            jwt.setMac(host);
            if (ipAddress != null) {
                int ipAddressSeparador = ipAddress.indexOf(",");
                if (ipAddressSeparador >= 0) {
                    ipAddress = ipAddress.substring(0, ipAddressSeparador);
                }
                //
                InetAddress addr = InetAddress.getByName(ipAddress);  // DOMAIN NAME from IP
                String host = addr.getHostName();
                //jwt.setIp(ipAddress);
                //jwt.setMac(host);
                jwt.setIp("127.0.0.0");
                jwt.setMac("onpe.gob.pe");
            }
            //
            String userAgent = request.getHeader("User-Agent");
            String os = "";
            String browser = "";
            jwt.setAgente(userAgent);
            if (userAgent.toLowerCase().indexOf("windows") >= 0) {
                os = "windows";
            } else if (userAgent.toLowerCase().indexOf("android") >= 0) {
                os = "android";
            } else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
                os = "iphone";
            } else if (userAgent.toLowerCase().indexOf("x11") >= 0) {
                os = "Unix";
            } else {
                os = "Desconocido";
            }


            String user = userAgent.toLowerCase();


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
                //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
                browser = "Netscape-?";
            } else if (user.contains("firefox")) {
                browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
            } else if (user.contains("rv")) {
                browser = "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
            } else {
                browser = "UnKnown";
            }

            jwt.setBrowser(browser);

            return jwt;
        } catch (UnknownHostException ex) {
            return null;
        }
    }

//    public static UserDto getUserFromToken(HttpServletRequest request) {
//        UserDto userDto = new UserDto();
//        String token = request.getHeader("Authorization").replace("Bearer ", "");
//        return userDto;
//    }

    public String encryJson(Object object) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return c.encrypt(new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create().toJson(object));
        } catch (Exception e) {
            return "";
        }
    }

    public JwtAuthentication dencryString(String validator) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            JwtAuthentication jwt = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create().fromJson(c.decrypt(validator), JwtAuthentication.class);
            return jwt;
        } catch (Exception e) {
            return null;
        }
    }


    public String encryStringB(String object) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return c.encrypt(object);
        } catch (Exception e) {
            return "";
        }
    }

    public String dencryStringB(String object) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            String d = c.decrypt(object);
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}
