package pe.gob.onpe.scebackend.exeption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponseAlternative;
import pe.gob.onpe.scebackend.security.exceptions.TokenExpiredException;

import java.time.LocalDateTime;

public class ResponseHelperException {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHelperException.class);

    private ResponseHelperException() {
        throw new IllegalStateException("Utility class");
    }

    private static <T> GenericResponseAlternative<T> buildResponse(
            boolean success,
            String message,
            T data,
            String errorCode) {
        return GenericResponseAlternative.<T>builder()
                .success(success)
                .message(message)
                .data(data)
                .exceptionCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> handleCommonExceptions(Exception e, String context) {

        if (e instanceof BusinessValidationException) {
            logger.warn("Error de validación de negocio en {}: {}", context, e.getMessage());
            return createBusinessErrorResponse(e.getMessage());

        } else if (e instanceof GenericException) {
            // Tratar GenericException como error de negocio para mantener compatibilidad
            logger.warn("Error genérico (tratado como negocio) en {}: {}", context, e.getMessage());
            return createBusinessErrorResponse(e.getMessage());

        } else if (e instanceof BadRequestException) {
            logger.error("Error de validación de datos en {}: {}", context, e.getMessage());
            return createBadRequestResponse(e.getMessage());

        } else if (e instanceof UnauthorizedException) {
            logger.error("Error de autorización en {}: {}", context, e.getMessage());
            return createUnauthorizedResponse(e.getMessage());

        } else if (e instanceof TokenExpiredException) {
            logger.error("Token expirado en {}: {}", context, e.getMessage());
            return createUnauthorizedResponse("Token expirado: " + e.getMessage());

        } else if (e instanceof NotFoundException) {
            logger.warn("Recurso no encontrado en {}: {}", context, e.getMessage());
            return createNotFoundResponse(e.getMessage());

        } else if (e instanceof InternalServerErrorException) {
            logger.error("Error interno específico en {}: {}", context, e.getMessage(),e);
            return createInternalServerErrorResponse(e.getMessage());

        }else if (e instanceof DuplicadoException) {
            logger.error("Error recurso duplicado en {}: {}", context, e.getMessage());
            return createConflictResponse(e.getMessage());

        } else if (e instanceof IllegalArgumentException) {
            logger.error("IllegalArgumentException,error de validación de datos en {}: {}", context, e.getMessage());
            return createBadRequestResponse(e.getMessage());
        }else {
            // Cualquier otra excepción no controlada
            logger.error("Error inesperado en {}", context, e);
            return createInternalServerErrorResponse("Error interno del sistema.");
        }
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createSuccessResponse(String message, T data) {
        return ResponseEntity.ok(buildResponse(true,message,data,null));
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createSuccessResponse(String message) {
        return createSuccessResponse(message, null);
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createBusinessErrorResponse(String message) {
        return ResponseEntity.ok(
                buildResponse(false, message, null, "BUSINESS_VALIDATION_ERROR")
        );
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createBadRequestResponse(String message) {
        return ResponseEntity.badRequest().body(
                buildResponse(false, message, null, "BAD_REQUEST")
        );
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createUnauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildResponse(false, message, null, "UNAUTHORIZED")
        );
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createNotFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildResponse(false, message, null, "NOT_FOUND")
        );
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createConflictResponse(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildResponse(false, message, null, "CONFLICT")
        );
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createInternalServerErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildResponse(false, message, null, "INTERNAL_SERVER_ERROR")
        );
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> createCustomResponse(
            String message, boolean success, T data, String errorCode, HttpStatus status) {
        return ResponseEntity.status(status).body(
                buildResponse(success, message, data, errorCode)
        );
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> handleValidationExceptions(Exception e, String context) {
        if (e instanceof BusinessValidationException || e instanceof GenericException) {
            logger.warn("Error de validación en {}: {}", context, e.getMessage());
            return createBusinessErrorResponse(e.getMessage());
        } else {
            return handleCommonExceptions(e, context);
        }
    }

    public static <T> ResponseEntity<GenericResponseAlternative<T>> handleAuthorizationExceptions(Exception e, String context) {
        if (e instanceof UnauthorizedException || e instanceof TokenExpiredException) {
            logger.error("Error de autorización en {}: {}", context, e.getMessage());
            return createUnauthorizedResponse(e.getMessage());
        } else {
            return handleCommonExceptions(e, context);
        }
    }

    public static void logException(Exception e, String context, String additionalInfo) {
        if (e instanceof BusinessValidationException || e instanceof GenericException) {
            logger.warn("Error de validación en {} {}: {}", context, additionalInfo, e.getMessage());
        } else {
            logger.error("Error en {} {}", context, additionalInfo, e);
        }
    }
}
