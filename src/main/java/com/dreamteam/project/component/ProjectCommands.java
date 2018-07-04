package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.model.Document;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.DocumentRepo;
import com.dreamteam.project.repository.ProjectRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

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
    private ConfigurationClass configurationClass;
    private UserRepo userRepo;

    @ShellMethod("Create new project (name, description, creator )")
    public String createProject(String name, String description, long creator) {
        Project project = new Project(null, name, creator, description);
        project = projectRepo.save(project);
        return project.toString();
    }

    @ShellMethod("Update project (id, name, description, creator )")
    public String updateProject(@ShellOption(defaultValue = "-1") String id, @ShellOption(defaultValue = "") String name, @ShellOption(defaultValue = "") String description, String creator) {
        try {
            Long projectId = Long.parseLong(id);
            Project project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + id + " cannot be found"));
            Long creatorId = Long.parseLong(creator);
            if (creatorId != -1) {
                User user = userRepo.findById(creatorId).orElseThrow(() -> new DBException("A user  with id " + creator + " cannot be found"));
                project.setCreatorId(creatorId);
            }
            if (!name.isEmpty())
                project.setProjectName(name);
            if (!description.isEmpty())
                project.setProjectDescription(description);
            project = projectRepo.save(project);
            return project.toString();
        } catch (DBException ex) {
            log.error("Cannot find project with id {}", id, ex);
            return "The project with id " + id + " cannot be found";
        } catch (NumberFormatException exn) {
            log.error("Error in parsing id", exn);
            return "Wrong id format";
        }


    }

    @ShellMethod("Show project details - which users are in which roles")
    public String detailProject(Long id) {
        try {
            Project project = projectRepo.findById(id).orElseThrow(() -> new DBException("A project with id " + id + " cannot be found"));
            //TODO to finished

        } catch (DBException e) {
            log.error("Cannot find project with id {}", id, e);
            return "The project with id " + id + " cannot be found";
        }

        return "mock";
    }


    @ShellMethod("Delete project by ID")
    public String deleteProject(Long id) {
        projectRepo.deleteById(id);
        return "Successfully deleted project with ID " + id;
    }

    @ShellMethod("Get list of projects")
    public String listAllProject() {
        return StreamSupport.stream(projectRepo.findAll().spliterator(), false)
                .map(Project::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod("Get list of projects of current user")
    public String listProject() {
        Long id = configurationClass.getUser().getUserId();
        //TODO finished listing
        return "";
    }
}
