package com.mycompany.project_p.repository;

import com.mycompany.project_p.model.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepo extends CrudRepository<Document, Long> {
    List<Document> findByName (String name);
}