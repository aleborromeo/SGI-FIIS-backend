package com.sgi.fiis.documentacion.infrastructure.persistence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sgi.fiis.documentacion.application.usecase.DownloadDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.UploadDocumentUseCase;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;

@Configuration
public class DocumentModuleConfig {

    @Bean
    public UploadDocumentUseCase uploadDocumentUseCase(DocumentRepositoryPort repositoryPort, FileStoragePort storagePort) {
        return new UploadDocumentUseCase(repositoryPort, storagePort);
    }

    @Bean
    public DownloadDocumentUseCase downloadDocumentUseCase(DocumentRepositoryPort repositoryPort, FileStoragePort storagePort) {
        return new DownloadDocumentUseCase(repositoryPort, storagePort);
    }
}