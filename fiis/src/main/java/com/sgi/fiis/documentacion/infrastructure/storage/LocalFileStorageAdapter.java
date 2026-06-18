package com.sgi.fiis.documentacion.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path rootLocation;

    // Lee la ruta desde tu application.yml, si no existe usa 'uploads-fiis' por defecto
    public LocalFileStorageAdapter(@Value("${storage.local.root:uploads-fiis}") String rootDir) {
        this.rootLocation = Paths.get(rootDir);
        try {
            // Crea la carpeta automáticamente si no existe al levantar el sistema
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo inicializar la carpeta de almacenamiento local de la FIIS", e);
        }
    }

    @Override
    public String store(InputStream fileStream, String fileName) {
        try {
            if (fileName == null) {
                throw new IllegalArgumentException("Nombre de archivo no válido o intento de Path Traversal.");
            }
            
            // Extraer únicamente el nombre base del archivo para evitar inyección de directorios/rutas absolutas
            String cleanedFileName = Paths.get(fileName).getFileName().toString();
            if (cleanedFileName.contains("..") || cleanedFileName.isEmpty() || fileName.contains("..")) {
                throw new IllegalArgumentException("Nombre de archivo no válido o intento de Path Traversal.");
            }
            
            // Renombrar con UUID para evitar que un alumno sobreescriba el archivo de otro si se llaman igual
            String uniqueName = UUID.randomUUID().toString() + "_" + cleanedFileName;
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueName)).normalize().toAbsolutePath();
            
            // Verificación defensiva adicional de que el archivo final sigue estando dentro de la raíz de almacenamiento
            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
                throw new IllegalArgumentException("Nombre de archivo no válido o intento de Path Traversal.");
            }
            
            Files.copy(fileStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return destinationFile.toString(); // Esta ruta física absoluta se guarda en la tupla de la BD
        } catch (IOException e) {
            throw new IllegalStateException("Fallo crítico al escribir el archivo en el disco del servidor.", e);
        }
    }

    @Override
    public InputStream load(String storagePath) {
        try {
            Path file = Paths.get(storagePath);
            if (Files.exists(file) && Files.isReadable(file)) {
                return Files.newInputStream(file);
            } else {
                throw new IllegalStateException("El archivo físico no existe en la ruta registrada o no se puede leer.");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error al abrir el flujo de lectura del archivo.", e);
        }
    }
}