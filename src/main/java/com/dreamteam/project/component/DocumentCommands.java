package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.model.Document;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.repository.DocumentRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import javax.annotation.PostConstruct;
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

    @ShellMethod("Create document (documentName, description, topic)")
    public String createDocument(String documentName, String desc, String topic) {
        Long creatorId = configurationClass.getUser().getUserId();
        Project project = configurationClass.getActualProject();
        Document document = new Document(null, documentName, desc, creatorId, topic, project);
        document = repo.save(document);
        return ("Document created succesfully: " + document);
    }

    @ShellMethodAvailability
    public Availability createDocumentAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
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

    @ShellMethodAvailability
    public Availability findDocumentByIdAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Update document")
    public String updateDocument(@ShellOption(defaultValue = "-1" ,value = "--id") Long id, @ShellOption(defaultValue = "") String documentName, @ShellOption(defaultValue = "") String desc, @ShellOption(defaultValue = "") String topic) throws DBException {
        try {
            Document document = repo.findById(id).orElseThrow(() -> new DBException("A document with id " + id + " cannot be found"));
            if (!documentName.isEmpty()) document.setDocumentName(documentName);
            if (!desc.isEmpty()) document.setDocumentDescription(desc);
            if (!topic.isEmpty()) document.setTopic(topic);
            document = repo.save(document);
            return "Successfully updated the document -> " + document.toString();
        } catch (DBException exception) {
            log.error("Cannot find document with id {}", id, exception);
            return exception.getMessage();
        }
    }

    @ShellMethodAvailability
    public Availability updateDocumentAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if(configurationClass.checkCreator(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("List all documents")
    public String listAllDocuments() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethodAvailability
    public Availability listAllDocumentsAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Delete document by ID")
    public String deleteDocumentById(Long id) {
        repo.deleteById(id);
        return "Successfully deleted document with ID " + id;
    }

    @ShellMethodAvailability
    public Availability deleteDocumentByIdAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("View document by name")
    public String viewDocumentByName(String name) {
        return repo.findByDocumentName(name).stream()
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethodAvailability
    public Availability viewDocumentByNameAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("View document by id")
    public String viewDocumentById(Long id) {
        return repo.findByDocumentId(id).stream()
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethodAvailability
    public Availability viewDocumentByIdAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("List documents of current user")
    public String listDocuments() {
        Long userId = configurationClass.getUser().getUserId();
        return repo.findByCreatorId(userId).stream()
                .map(Document::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethodAvailability
    public Availability listDocumentsAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @PostConstruct
    public void loadPermissions() {
        permissions = configurationClass.loadPermissions(this.getClass().getSimpleName());
        if(permissions.isEmpty()){
            log.error("Document permissions are not ready to use");
        }
    }
}