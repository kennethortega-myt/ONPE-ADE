package pe.gob.onpe.sceorcbackend.rest.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BindException.class)
  public ResponseEntity<GenericResponse<Object>> handleBindException(BindException ex) {
    Map<String, String> errors = ex.getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> {
                      if ("numeroPaginas".equals(error.getField())) {
                        return "El campo 'numeroPaginas' debe ser un número entero válido.";
                      }
                      return error.getDefaultMessage();
                    },
                    (msg1, msg2) -> msg1
            ));

    GenericResponse<Object> response = new GenericResponse<>(
            false,
            "Error de validación",
            errors
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // Para parámetros con tipo inválido en PathVariable o QueryParam
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<GenericResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String mensaje = "El campo '" + ex.getName() + "' tiene un valor inválido.";
    if ("numeroPaginas".equals(ex.getName())) {
      mensaje = "El campo 'numeroPaginas' debe ser un número entero válido.";
    }
    return ResponseEntity.badRequest().body(new GenericResponse<>(false, mensaje, null));
  }




  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GenericResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> {
                      if ("numeroPaginas".equals(error.getField())
                              && error.getDefaultMessage().startsWith("Failed to convert")) {
                        return "El campo 'numeroPaginas' debe ser un número entero válido.";
                      }
                      return error.getDefaultMessage();
                    },
                    (msg1, msg2) -> msg1
            ));

    GenericResponse<Object> response = new GenericResponse<>(
            false,
            "Error de validación",
            errors
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // Manejo específico para RuntimeException (opcional, si quieres diferenciar)
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<GenericResponse<Object>> handleRuntime(RuntimeException ex) {

    return ResponseHelperException.handleCommonExceptions(ex, "");
  }

  // Fallback general para cualquier otra excepción no contemplada
  @ExceptionHandler(Exception.class)
  public ResponseEntity<GenericResponse<Object>> handleGeneral(Exception ex) {
    return ResponseHelperException.handleCommonExceptions(ex, "");
  }
  
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GenericResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
      return ResponseEntity
              .badRequest()
              .body(new GenericResponse<>(false, ex.getMessage(), null));
  }

}
