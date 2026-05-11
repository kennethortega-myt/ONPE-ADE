package pe.gob.onpe.scebackend.multitenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.service.impl.ConfiguracionProcesoElectoralService;

import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class TenantContextAspect {
    private final ConfiguracionProcesoElectoralService procesoElectoralService;

    @Around("@annotation(pe.gob.onpe.scebackend.utils.anotation.SetTenantContext)")
    public Object setTenantContext(ProceedingJoinPoint joinPoint) throws Throwable {
        // Obtener el proceso vigente
        List<ConfiguracionProcesoElectoral> procesos = procesoElectoralService.listarVigentesYActivos();

        if (procesos == null || procesos.isEmpty()) {
            log.warn("No hay procesos electorales vigentes y activos");
            return null;
        }

        String acronimo = procesos.get(0).getAcronimo();
        CurrentTenantId.set(acronimo);

        try {
            // Ejecutar el método
            return joinPoint.proceed();
        } finally {
            // Limpiar siempre, incluso si hay excepción
            CurrentTenantId.clear();
        }
    }
}
