package pe.gob.onpe.scebackend.utils;

import java.io.File;

import pe.gob.onpe.scebackend.exeption.CreacionDirectorioException;

public class DirectorioUtils {
	
	private DirectorioUtils() {
        throw new UnsupportedOperationException("DirectorioUtils es una clase utilitaria y no debe ser instanciada");
    }

    /**
     * Crea una estructura de directorios anidados dentro de una base existente.
     *
     * @param basePath Ruta base que ya debe existir.
     * @param subDir Primer subdirectorio dentro de la base.
     * @param nestedSubDir Subdirectorio opcional dentro de subDir.
     * @return El path final si fue exitoso, o el path base si falló.
     */
    public static String construirRutaConDirectorios(String basePath, String subDir, String nestedSubDir) {
        try {
            File base = new File(basePath);
            if (!base.exists() || !base.isDirectory()) {
                throw new IllegalArgumentException("La ruta base no existe o no es un directorio");
            }

            File subDirFile = new File(base, subDir);
            if (!subDirFile.exists()) {
                boolean creado = subDirFile.mkdirs();
                if (!creado) throw new CreacionDirectorioException("No se pudo crear el subdirectorio");
            }

            if (nestedSubDir != null && !nestedSubDir.isEmpty()) {
                File nestedFile = new File(subDirFile, nestedSubDir);
                if (!nestedFile.exists()) {
                    boolean creado = nestedFile.mkdirs();
                    if (!creado) throw new CreacionDirectorioException("No se pudo crear el subdirectorio anidado");
                }
                return nestedFile.getAbsolutePath();
            }

            return subDirFile.getAbsolutePath();
        } catch (Exception e) {
            return basePath;
        }
    }
}