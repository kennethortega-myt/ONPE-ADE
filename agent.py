import os

# CONFIGURACIÓN
RUTA_OBJETIVO = r'C:\Users\User\Desktop\ADENDA-ONPE'
LIMITE_MB = 100
LIMITE_BYTES = LIMITE_MB * 1024 * 1024

# Extensiones que SÍ queremos conservar (compatibles con SonarCloud)
EXT_CODIGO = {
    '.py', '.java', '.js', '.ts', '.c', '.cpp', '.h', '.hpp',
    '.cs', '.php', '.go', '.rb', '.sql', '.xml', '.json',
    '.yaml', '.yml', '.md', '.properties', '.html', '.css'
}

def ejecutar_limpieza(ruta):
    print(f"Iniciando barrido en: {ruta}")
    total_borrados = 0
    espacio_libre = 0

    for raiz, carpetas, archivos in os.walk(ruta, topdown=True):
        # Evitar tocar la configuración de git si existiera
        if '.git' in carpetas:
            carpetas.remove('.git')

        for archivo in archivos:
            ruta_completa = os.path.join(raiz, archivo)
            _, ext = os.path.splitext(archivo)
            
            try:
                tamano = os.path.getsize(ruta_completa)
                debe_eliminar = False

                # 1. ¿Es demasiado pesado?
                if tamano > LIMITE_BYTES:
                    print(f"[ELIMINADO - PESADO] {archivo} ({tamano/(1024*1024):.2f} MB)")
                    debe_eliminar = True
                
                # 2. ¿Es un archivo de código útil?
                elif ext.lower() not in EXT_CODIGO:
                    print(f"[ELIMINADO - TIPO] {archivo}")
                    debe_eliminar = True

                if debe_eliminar:
                    os.remove(ruta_completa)
                    total_borrados += 1
                    espacio_libre += tamano

            except Exception as e:
                print(f"[ERROR] No se pudo procesar {archivo}: {e}")

    print(f"\nLimpieza completada. Archivos eliminados: {total_borrados}")
    print(f"Espacio liberado: {espacio_libre/(1024*1024):.2f} MB")

if __name__ == '__main__':
    ejecutar_limpieza(RUTA_OBJETIVO)