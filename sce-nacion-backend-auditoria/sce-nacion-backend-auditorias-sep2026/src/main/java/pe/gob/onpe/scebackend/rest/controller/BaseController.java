package pe.gob.onpe.scebackend.rest.controller;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.Base64;
import java.util.List;

@Controller
public class BaseController {

    protected final TokenDecoder tokenDecoder;
    public static final String MSG_REPORTE_GENERADO = "Reporte generado";
    public static final String MSG_REPORTE_SIN_DATA = "No existen coincidencias para el filtro seleccionado";

    public BaseController(TokenDecoder tokenDecoder) {
        this.tokenDecoder = tokenDecoder;
    }

    protected LoginUserHeader getUserLogin(final String authorization){
        final LoginUserHeader user = new LoginUserHeader();
        if (authorization != null) {
            String token = authorization.substring(SceConstantes.LENGTH_BEARER);
            Claims claims = this.tokenDecoder.decodeToken(token);
            user.setPerfil(claims.get("per", String.class));
            user.setUsuario(claims.get("usr", String.class));
        }
        return user;
    }

    protected ResponseEntity<GenericResponse> getListResponse(List<?> list) {
        HttpStatus httpStatus = HttpStatus.OK;
        GenericResponse genericResponse = new GenericResponse();

        if (list != null && !list.isEmpty()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(list);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
        }

        return new ResponseEntity<>(genericResponse, httpStatus);
    }

    protected ResponseEntity<GenericResponse> getPdfResponse(byte[] data) {
        HttpStatus httpStatus = HttpStatus.OK;
        if (data != null && data.length > 0) {
            String encodedString = Base64.getEncoder().encodeToString(data);
            GenericResponse genericResponse = new GenericResponse();
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(encodedString);
            return new ResponseEntity<>(genericResponse, httpStatus);
        } else {
            GenericResponse genericResponse = new GenericResponse();
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
            return new ResponseEntity<>(genericResponse, httpStatus);
        }

    }

    protected ResponseEntity<GenericResponse> getObjectResponse(Object object) {
        GenericResponse genericResponse = new GenericResponse();
        if (object != null) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(object);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }

    protected ResponseEntity<GenericResponse> getErrorValidacionResponse(String mensaje) {
        HttpStatus httpStatus = HttpStatus.OK;
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setMessage(mensaje);
        genericResponse.setSuccess(Boolean.FALSE);
        return new ResponseEntity<>(genericResponse, httpStatus);
    }

}
