package pe.gob.onpe.sceorcbackend.exception.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pe.gob.onpe.sceorcbackend.exception.*;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;

public class ResponseHelperException {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHelperException.class);
    private ResponseHelperException() {
        throw new IllegalStateException("Utility class");
    }
    /**
     *
     * @param e Excepción a manejar
     * @param context Contexto donde ocurrió la excepción (para logging)
     * @return ResponseEntity apropiado según el tipo de excepción
     */
    public static <T> ResponseEntity<GenericResponse<T>> handleCommonExceptions(Exception e, String context) {

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

    public static <T> ResponseEntity<GenericResponse<T>> handleCommonExceptions(Exception e) {
        String context = getCallerContext();
        return handleCommonExceptions(e, context);
    }
    private static String getCallerContext() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3];
            String className = caller.getClassName();
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            String methodName = caller.getMethodName();
            return simpleClassName + "." + methodName;
        }
        return "Unknown";
    }

    // ================================================================================================
    // MÉTODOS HELPER PARA CREAR RESPUESTAS EXITOSAS
    // ================================================================================================

    /**
     * Crea una respuesta exitosa con datos
     */
    public static <T> ResponseEntity<GenericResponse<T>> createSuccessResponse(String message, T data) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(true);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una respuesta exitosa sin datos
     */
    public static <T> ResponseEntity<GenericResponse<T>> createSuccessResponse(String message) {
        return createSuccessResponse(message, null);
    }

    // ================================================================================================
    // MÉTODOS HELPER PARA CREAR RESPUESTAS DE ERROR ESPECÍFICAS
    // ================================================================================================

    /**
     * Crea una respuesta de error de negocio (HTTP 200 con success = false)
     * Para errores de validación de reglas de negocio que el usuario puede corregir
     */
    public static <T> ResponseEntity<GenericResponse<T>> createBusinessErrorResponse(String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(false);
        response.setData(null);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una respuesta de solicitud incorrecta (HTTP 400)
     * Para errores de validación de datos de entrada
     */
    public static <T> ResponseEntity<GenericResponse<T>> createBadRequestResponse(String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(false);
        response.setData(null);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Crea una respuesta de no autorizado (HTTP 401)
     * Para errores de autenticación y autorización
     */
    public static <T> ResponseEntity<GenericResponse<T>> createUnauthorizedResponse(String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(false);
        response.setData(null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Crea una respuesta de no encontrado (HTTP 404)
     * Para recursos que no existen
     */
    public static <T> ResponseEntity<GenericResponse<T>> createNotFoundResponse(String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(false);
        response.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Crea una respuesta de no conflicto (HTTP 404)
     * Para recursos que ya existen
     */
    public static <T> ResponseEntity<GenericResponse<T>> createConflictResponse(String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(false);
        response.setData(null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }


    /**
     * Crea una respuesta de error interno del servidor (HTTP 500)
     * Para errores técnicos o del sistema
     */
    public static <T> ResponseEntity<GenericResponse<T>> createInternalServerErrorResponse(String message) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(false);
        response.setData(null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Crea una respuesta personalizada con status HTTP específico
     */
    public static <T> ResponseEntity<GenericResponse<T>> createCustomResponse(String message, boolean success, T data, HttpStatus status) {
        GenericResponse<T> response = new GenericResponse<>();
        response.setMessage(message);
        response.setSuccess(success);
        response.setData(data);
        return ResponseEntity.status(status).body(response);
    }

    // ================================================================================================
    // MÉTODOS HELPER ADICIONALES PARA CASOS ESPECÍFICOS
    // ================================================================================================

    /**
     * Maneja específicamente excepciones de validación (BusinessValidationException y GenericException)
     * como errores de negocio
     */
    public static <T> ResponseEntity<GenericResponse<T>> handleValidationExceptions(Exception e, String context) {
        if (e instanceof BusinessValidationException || e instanceof GenericException) {
            logger.warn("Error de validación en {}: {}", context, e.getMessage());
            return createBusinessErrorResponse(e.getMessage());
        } else {
            return handleCommonExceptions(e, context);
        }
    }

    /**
     * Maneja excepciones de autorización de forma unificada
     */
    public static <T> ResponseEntity<GenericResponse<T>> handleAuthorizationExceptions(Exception e, String context) {
        if (e instanceof UnauthorizedException || e instanceof TokenExpiredException) {
            logger.error("Error de autorización en {}: {}", context, e.getMessage());
            return createUnauthorizedResponse(e.getMessage());
        } else {
            return handleCommonExceptions(e, context);
        }
    }

    /**
     * Método de conveniencia para logging con contexto adicional
     */
    public static void logException(Exception e, String context, String additionalInfo) {
        if (e instanceof BusinessValidationException || e instanceof GenericException) {
            logger.warn("Error de validación en {} {}: {}", context, additionalInfo, e.getMessage());
        } else {
            logger.error("Error en {} {}", context, additionalInfo, e);
        }
    }
}
