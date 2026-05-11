package pe.gob.onpe.scebackend.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class LoggingUtil {
  private LoggingUtil() {
    throw new UnsupportedOperationException("LoggingUtil es una clase utilitaria y no debe ser instanciada");
  }
  public static void logTrace(String functionName, String serviceName, Object inputData, String spanId,
      boolean isError, Exception exception) {

    if (spanId == null) {
      spanId = UUID.randomUUID().toString();
    }

    MDC.put("span-id", spanId);

    Map<String, Object> logMap = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order
    logMap.put("Timestamp", OffsetDateTime.now().toString());
    logMap.put("SeverityText", isError ? "Error" : "Information");

    addAtributes(functionName, serviceName, inputData, logMap);

    Map<String, Object> body = new HashMap<>();
    body.put("message", isError ? exception.getMessage() : "Incoming HTTP request");
    body.put("event_type", "MethodExecution");
    logMap.put("Body", body);

    Map<String, Object> resource = new HashMap<>();
    resource.put("project.name", "sce-nacion-backend");
    resource.put("service.name", "SCE");

    logMap.put("Resource", resource);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String logMessage = gson.toJson(logMap);

    try {
      if (isError) {
        log.error(logMessage, exception);
      } else {
        log.info(logMessage);
      }
    } finally {
      MDC.clear();
    }
  }

  public static String logTransactions(String functionName, String serviceName, Object inputData, String message, String eventType) {

    MDC.put("span-id", UUID.randomUUID().toString());

    Map<String, Object> logMap = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order
    logMap.put("Timestamp", OffsetDateTime.now().toString());
    logMap.put("SeverityText", "Information");

    addAtributes(functionName, serviceName, inputData, logMap);

    Map<String, Object> body = new HashMap<>();
    body.put("message", message);
    body.put("event_type", Objects.isNull(eventType) ? "MethodExecution" : eventType);
    logMap.put("Body", body);

    Map<String, Object> resource = new HashMap<>();
    resource.put("project.name", "sce-nacion-backend");
    resource.put("service.name", serviceName);

    logMap.put("Resource", resource);

    Gson gson = new GsonBuilder().create();
    String logMessage = gson.toJson(logMap);

    try {
      return logMessage;
    } finally {
      MDC.clear();
    }
  }

  private static void addAtributes(String functionName, String serviceName, Object inputData, Map<String, Object> logMap) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("process.pid", ProcessHandle.current().pid());
    attributes.put("thread.name", Thread.currentThread().getName());
    attributes.put("code.namespace", serviceName);
    attributes.put("code.function", functionName);
    attributes.put("code.request", inputData);
    logMap.put("Attributes", attributes);
  }
}