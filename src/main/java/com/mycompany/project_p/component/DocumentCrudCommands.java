package com.mycompany.project_p.component;

import com.mycompany.project_p.model.Document;
import com.mycompany.project_p.repository.DocumentRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@ShellComponent
@ShellCommandGroup("Document commands")
public class DocumentCrudCommands {

    private DocumentRepo repo;

    public String createDocument(String name, String description, String creator, String topic){
        Document document = new Document(null, name, description, creator, topic);
        document = repo.save(document);
        return "Project created succesfully.";
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