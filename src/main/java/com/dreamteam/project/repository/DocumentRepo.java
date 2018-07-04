package com.dreamteam.project.repository;

import com.dreamteam.project.model.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepo extends CrudRepository<Document, Long> {
    List<Document> findByDocumentName (String name);
    List<Document> findByCreatorId (Long id);
}