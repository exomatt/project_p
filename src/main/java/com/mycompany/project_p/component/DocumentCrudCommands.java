package com.mycompany.project_p.component;

import com.mycompany.project_p.exeption.DBException;
import com.mycompany.project_p.model.Document;
import com.mycompany.project_p.repository.DocumentRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ShellComponent
@ShellCommandGroup("Document commands")
public class DocumentCrudCommands {

    private DocumentRepo repo;


    public String createDocument(String name, String description, Long creatorID, String topic){
        Document document = new Document(null, name, description, creatorID, topic);
        document = repo.save(document);
        return "Project created succesfully.";
    }

    @ShellMethod("Find document by ID")
    public String findById(Long id) throws DBException {
        try {
            Document document = repo.findById(id).orElseThrow(() -> new DBException("A person with id " + id + " cannot be found"));;
            return "Successfully found document -> " + document;
        } catch (DBException exception) {
            log.error("Cannot find document with id {}", id, exception);
            return "The document with id " + id + " cannot be found";
        }
    }

    @ShellMethod("Update document")
    public String update(Long id, String name, String description, String creator, String topic) {
        Document document = new Document(null, name, description, creator, topic);
        document = repo.save(document);
        return "Successfully updated the document -> " + document;
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