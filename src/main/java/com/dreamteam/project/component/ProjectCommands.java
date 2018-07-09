package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigReader;
import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Document;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.Role;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.DocumentRepo;
import com.dreamteam.project.repository.ProjectRepo;
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
@ShellCommandGroup("Project commands")
public class ProjectCommands {
    private final ProjectRepo projectRepo;
    private final AssigmentRepo assigmentRepo;
    private final DocumentRepo documentRepo;
    private final ConfigurationClass configurationClass;
    private final UserRepo userRepo;
    private Map<String, List<String>> permissions = new HashMap<>();

    @ShellMethod("Create new project (name, description)")
    public String createProject(String name, String description) {
        Long creatorId = configurationClass.getUser().getUserId();
        Project project = new Project(null, name, creatorId, description);
        project = projectRepo.save(project);
        Role role = Role.valueOf("Creator");
        assigmentRepo.save(new Assigment(configurationClass.getUser(), role, project));
        return project.toString();
    }

    @ShellMethodAvailability
    public Availability createProjectAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Update project (id, name, description, creator )")
    public String updateProject(@ShellOption(defaultValue = "-1", value = "-i") Long projectId, @ShellOption(defaultValue = "", value = "-n") String name, @ShellOption(defaultValue = "", value = "-d") String description) throws DBException {
        try {
            Project project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + projectId + " cannot be found"));
            if (!name.isEmpty())
                project.setProjectName(name);
            if (!description.isEmpty())
                project.setProjectDescription(description);
            project = projectRepo.save(project);
            return project.toString();
        } catch (NumberFormatException exn) {
            log.error("Error in parsing id", exn);
            return exn.getMessage();
        } catch (DBException exception) {
            log.error("Cannot find project with id {}", projectId, exception);
            return exception.getMessage();
        }
    }

    @ShellMethodAvailability
    public Availability updateProjectAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Project was not chosen");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Show project details - which users are in which roles")
    public String detailProject() {
        Project project = configurationClass.getActualProject();
        return project.toString() + assigmentRepo.findByProjectProjectId(project.getProjectId()).stream()
                .map(Assigment::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethodAvailability
    public Availability detailProjectAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Project was not chosen");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }


    @ShellMethod("Delete project by ID (if you wanna migrate documents please enter project id as second value)")
    public String deleteProject(Long deletedProjectId, @ShellOption(defaultValue = "-1") Long migrateProjectId) {

        if (migrateProjectId == -1) {
            deleteAssigment(deletedProjectId);
            projectRepo.deleteById(deletedProjectId);
            configurationClass.setActualProject(null);
            return "Successfully deleted project with ID " + deletedProjectId + " and logout of it";
        }
        try {
            Project projectDeleted = projectRepo.findById(deletedProjectId).orElseThrow(() -> new DBException("A project with id " + deletedProjectId + " cannot be found"));
            Project projectMigration = projectRepo.findById(migrateProjectId).orElseThrow(() -> new DBException("A project with id " + migrateProjectId + " cannot be found"));
            List<Document> documentListToMigration = projectDeleted.getDocuments();
            if (documentListToMigration.isEmpty()) {
                throw new DBException("In project " + deletedProjectId + " there aren't documents");
            }
            List<Document> documentList = projectMigration.getDocuments();
            for (Document doc:documentListToMigration) {
                Document document = new Document();
                document.setDocumentName(doc.getDocumentName());
                document.setTopic(doc.getTopic());
                document.setDocumentDescription(doc.getDocumentDescription());
                document.setCreatorId(doc.getCreatorId());
                document.setProject(projectMigration);
                documentRepo.save(document);
                projectMigration.addToList(document);

            }
            projectDeleted.setDocuments(null);
            projectRepo.save(projectMigration);

            deleteAssigment(deletedProjectId);
            projectRepo.deleteById(deletedProjectId);
            configurationClass.setActualProject(null);
            return "Successfully deleted project with ID " + deletedProjectId + ", migrate document to " + migrateProjectId + " and logout of it";
        } catch (DBException e) {
            log.error("Migration documents is not working", e);
            return e.getMessage();
        }
    }

    public void deleteAssigment(Long id) {
        List<Assigment> assigments = assigmentRepo.findByProjectProjectId(id);
        if (assigments.isEmpty()) {
            return;
        }
        for (Assigment as : assigments) {
            assigmentRepo.delete(as);
        }
    }

    @ShellMethodAvailability
    public Availability deleteProjectAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Project was not chosen");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Get list of projects")
    public String listAllProjects() {
        return StreamSupport.stream(projectRepo.findAll().spliterator(), false)
                .map(Project::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethodAvailability
    public Availability listAllProjectsAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (!configurationClass.getUser().getLastName().equals("Administrator")) {
            return Availability.unavailable("You are not a admin");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Get list of projects of current user")
    public String listProjects() {
        Long id = configurationClass.getUser().getUserId();
        try {
            List<Assigment> assigments = assigmentRepo.findByUserUserId(id);
            String projects = "Your projects: ";
            if (!assigments.isEmpty()) {

                for (Assigment ass : assigments) {
                    projects = projects.concat("\n" + ass.getProject().toString());
                }
                return projects;
            } else {
                return "You have no projects";
            }
        } catch (IllegalArgumentException e) {
            log.error("You are not logged in", e);
            return "Cannot find role";
        }
    }

    @ShellMethodAvailability
    public Availability listProjectsAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @PostConstruct
    public void loadPermissions() {
        permissions = ConfigReader.loadPermissions(this.getClass().getSimpleName());
    }
}
