package com.sgi.fiis.documentacion.domain.port;

import com.sgi.fiis.documentacion.domain.model.Document;
import java.util.List;
import java.util.Optional;

public interface DocumentRepositoryPort {
    Document save(Document document);
    Optional<Document> findById(Long id);
    List<Document> findAll();
}