package com.dreamteam.project.component;

import com.dreamteam.project.model.Document;
import com.dreamteam.project.repository.DocumentRepo;
import com.dreamteam.project.exeption.DBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@ShellComponent
@ShellCommandGroup("Document commands")
public class DocumentCommands {

    private DocumentRepo repo;

    @Autowired
    public DocumentCommands() {
    }

    public DocumentCommands(DocumentRepo repo) {
        this.repo = repo;
    }


    @ShellMethod("Create document")
    public String createDocument(String documentName, String desc, Long creatorId, String topic) {
        Document document = new Document(null, documentName, desc, creatorId, topic);
        document = repo.save(document);
        return "Project created succesfully.";
    }

    @ShellMethod("Find document by ID")
    public String findById(Long id) throws DBException {
        try {
            Document document = repo.findById(id).orElseThrow(() -> new DBException("A person with id " + id + " cannot be found"));
            return "Successfully found document -> " + document;
        } catch (DBException exception) {
            log.error("Cannot find document with id {}", id, exception);
            return "The document with id " + id + " cannot be found";
        }
    }

    @ShellMethod("Update document")
    public String update(Long id, @ShellOption(defaultValue = "") String documentName, @ShellOption(defaultValue = "") String desc, Long creatorId, @ShellOption(defaultValue = "") String topic) throws DBException {
        try {
            Document document = repo.findById(id).orElseThrow(() -> new DBException("A person with id " + id + " cannot be found"));
            if (!documentName.isEmpty()) document.setDocumentName(documentName);
            if (!desc.isEmpty()) document.setDocumentDescription(desc);
            if (!topic.isEmpty()) document.setTopic(topic);
            document = repo.save(document);
            return "Successfully updated the document -> " + document;
        } catch (DBException exception) {
            log.error("Cannot find document with id {}", id, exception);
            return "The document with id " + id + " cannot be updated because it cannot be found";
        }
    }

    @ShellMethod("Find all documents")
    public String findAll() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod("Delete document by ID")
    public String deleteById(Long id) {
        repo.deleteById(id);
        return "Successfully deleted document with ID " + id;
    }

    @ShellMethod("Find documents by name")
    public String findByName(String name) {
        return repo.findByName(name).stream()
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }
}