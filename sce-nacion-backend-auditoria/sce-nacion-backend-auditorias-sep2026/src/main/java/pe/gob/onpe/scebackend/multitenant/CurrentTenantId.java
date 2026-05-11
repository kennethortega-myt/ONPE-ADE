package pe.gob.onpe.scebackend.multitenant;

public class CurrentTenantId {

    private CurrentTenantId() {
        throw new IllegalStateException("Utility class");
    }

	private static final ThreadLocal<String> threadLocalTenantId = new ThreadLocal<>();
    
    public static String get() {
        return threadLocalTenantId.get();
    }
    
    public static void set(String tenantId) {
        threadLocalTenantId.set(tenantId);
    }
    
    public static void clear() {
        threadLocalTenantId.remove();
    }
	
}
