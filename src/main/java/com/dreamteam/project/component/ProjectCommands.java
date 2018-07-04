package com.dreamteam.project.component;

import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.model.Document;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.DocumentRepo;
import com.dreamteam.project.repository.ProjectRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ShellComponent
@ShellCommandGroup("Project commands")
public class ProjectCommands {
    private ProjectRepo projectRepo;
    private AssigmentRepo assigmentRepo;
    private DocumentRepo documentRepo;

    @ShellMethod("Create new project (name, description, creator )")
    public String createProject(String name, String description, long creator) {
        Project project = new Project(null, name, creator, description);
        project = projectRepo.save(project);
        return project.toString();
    }

    @ShellMethod("Update project (id, name, description, creator )")
    public String updateProject(Long id, String name, String description, Long creator) {
        try {
            Project project = projectRepo.findById(id).orElseThrow(() -> new DBException("A project with id " + id + " cannot be found"));
            project.setProjectName(name);
            project.setCreatorId(creator);
            project.setProjectDescription(description);
            project = projectRepo.save(project);
            return project.toString();
        } catch (DBException e) {
            log.error("Cannot find project with id {}", id, e);
            return "The project with id " + id + " cannot be found";
        }


    }

    @ShellMethod("Show project details - which users are in which roles")
    public String detailProject(Long id) throws DBException {
        try {
            Project project = projectRepo.findById(id).orElseThrow(() -> new DBException("A project with id " + id + " cannot be found"));


            return "";
        } catch (DBException e) {
            log.error("Cannot find project with id {}", id, e);
            return "The project with id " + id + " cannot be found";
        }

    }

    @ShellMethod("Delete project by ID")
    public String deleteProject(Long id) {
        projectRepo.deleteById(id);
        return "Successfully deleted project with ID " + id;
    }

    @ShellMethod("Get list of projects")
    public String listProject() {
        return StreamSupport.stream(projectRepo.findAll().spliterator(), false)
                .map(Project::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod("Ad document do project (project ID , document ID)")
    public String addDocument(Long projectId, Long documentId) {
        Project project;
        Document document;
        try {
            document = documentRepo.findById(documentId).orElseThrow(() -> new DBException("A document with id " + id + " cannot be found"));
        } catch (DBException e) {
            log.error("Cannot find document with id {}", id, e);
            return "The document with id " + id + " cannot be found";
        }

        try {
            project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + id + " cannot be found"));
        } catch (DBException d) {
            log.error("Cannot find project with id {}", id, d);
            return "The project with id " + id + " cannot be found";
        }
        project.addToList(document);
        document.setProject(project);

        return "";
    }
}
