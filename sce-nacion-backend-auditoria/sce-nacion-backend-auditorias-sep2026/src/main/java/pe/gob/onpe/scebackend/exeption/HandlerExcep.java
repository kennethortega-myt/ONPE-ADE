package pe.gob.onpe.scebackend.exeption;

import net.sf.jasperreports.engine.JRException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pe.gob.onpe.scebackend.exeption.entity.BaseExcep;
import pe.gob.onpe.scebackend.exeption.entity.ViolationFieldConstraint;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class HandlerExcep {

    @ExceptionHandler({JRException.class,NullPointerException.class,
            BadSqlGrammarException.class,
            PSQLException.class,
            UncategorizedSQLException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    BaseExcep onConstraintGeneral(
            Exception e) {
        BaseExcep error = new BaseExcep();
        error.setExceptionCode("0");
        error.setExceptionMessage("Ocurrió un error interno");

        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    BaseExcep onConstraintValidationException(
            ConstraintViolationException e) {
        BaseExcep error = new BaseExcep();
        error.setExceptionCode("0");
        error.setExceptionMessage("Error en la validación de campos");
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getViolationsFields().add(
                    new ViolationFieldConstraint(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    BaseExcep onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        BaseExcep error = new BaseExcep();
        error.setExceptionCode("0");
        error.setExceptionMessage("Error en la validación de campos");

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolationsFields().add(
                    new ViolationFieldConstraint(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }
}
