package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.model.Document;
import com.dreamteam.project.repository.DocumentRepo;
import com.dreamteam.project.repository.UserRepo;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ShellComponent
@ShellCommandGroup("Document commands")
public class DocumentCommands {

    private final DocumentRepo repo;
    private final UserRepo uRepo;
    private final ConfigurationClass configurationClass;
    private Map<String, List<String>> permissions = new HashMap<>();

    @ShellMethod("Create document (documentName, description, creatorID, topic)")
    public String createDocument(String documentName, String desc, Long creatorId, String topic) {
        Document document = new Document(null, documentName, desc, creatorId, topic);
        document = repo.save(document);
        return ("Document created succesfully: " + document);
    }

    @ShellMethod("Find document by ID")
    public String findDocumentById(Long id) throws DBException {
        try {
            Document document = repo.findById(id).orElseThrow(() -> new DBException("A person with id " + id + " cannot be found"));
            return "Successfully found document -> " + document;
        } catch (DBException exception) {
            log.error("Cannot find document with id {}", id, exception);
            return "The document with id " + id + " cannot be found";
        }
    }

    @ShellMethod("Update document")
    public String updateDocument(Long id, @ShellOption(defaultValue = "") String documentName, @ShellOption(defaultValue = "") String desc, @ShellOption(defaultValue = "-1") String creatorId, @ShellOption(defaultValue = "") String topic) throws DBException {
        try {
            Document document = repo.findById(id).orElseThrow(() -> new DBException("A document with id " + id + " cannot be found"));
            Long creator = Long.parseLong(creatorId);
            //TODO
            /*What is a better way to validate (without further usage of the object) weather an object is in the database?

            //  1
            if (creator >= 0) {
                uRepo.findById(creator).orElseThrow(() -> new DBException("A creator with id " + id + " cannot be found"));
                document.setCreatorId(creator);
            }

            //  2
            if (creator >= 0 && uRepo.existsById(creator)) {
                document.setCreatorId(creator);
            }
            */
            if (!documentName.isEmpty()) document.setDocumentName(documentName);
            if (!desc.isEmpty()) document.setDocumentDescription(desc);
            if (!topic.isEmpty()) document.setTopic(topic);
            document = repo.save(document);
            return "Successfully updated the document -> " + document;
        } catch (NumberFormatException exception) {
            log.error("CreatorId {} is not a number", id, exception);
            return "CreatorId " + id + " is not a number, document cannot be updated";
        }
    }

    @ShellMethod("List all documents")
    public String listAllDocuments() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod("Delete document by ID")
    public String deleteDocumentById(Long id) {
        repo.deleteById(id);
        return "Successfully deleted document with ID " + id;
    }

    @ShellMethod("Find documents by name")
    public String findDocumentByName(String name) {
        return repo.findByDocumentName(name).stream()
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod("List documents of current user")
    public String listUserDocuments() {
        Long userId = configurationClass.getUser().getUserId();
        return repo.findByCreatorId(userId).stream()
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @PostConstruct
    public void loadPermissions() {
        permissions = configurationClass.loadPermissions(this.getClass().getSimpleName());
    }
}