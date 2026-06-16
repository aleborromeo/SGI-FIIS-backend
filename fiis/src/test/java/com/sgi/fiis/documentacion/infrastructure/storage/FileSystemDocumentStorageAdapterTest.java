package com.sgi.fiis.documentacion.infrastructure.storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Infraestructura - LocalFileStorageAdapter")
class LocalFileStorageAdapterTest {

    @TempDir
    Path sharedTempDir;

    @Test
    @DisplayName("Debe guardar y recuperar documentos físicos correctamente")
    void testStorageOperations() throws Exception {

        // Carpeta temporal aislada para el test
        String rootPath = sharedTempDir.toAbsolutePath().toString();

        // Instanciar adapter real
        LocalFileStorageAdapter storageAdapter =
                new LocalFileStorageAdapter(rootPath);

        String fakeFileName = "proyecto_base.pdf";

        ByteArrayInputStream mockStream =
                new ByteArrayInputStream(
                        "bytes-de-prueba-fiis".getBytes()
                );

        // Guardar archivo
        String savedPath = storageAdapter.store(
                mockStream,
                fakeFileName
        );

        assertNotNull(savedPath);

        // Verificar que se creó físicamente
        assertTrue(Path.of(savedPath).toFile().exists());

        // Recuperar archivo
        InputStream retrievedStream =
                storageAdapter.load(savedPath);

        assertNotNull(retrievedStream);

        byte[] readBytes =
                retrievedStream.readAllBytes();

        assertEquals(
                "bytes-de-prueba-fiis",
                new String(readBytes)
        );

        retrievedStream.close();
    }
}