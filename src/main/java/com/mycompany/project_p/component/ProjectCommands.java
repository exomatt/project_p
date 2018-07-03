package com.mycompany.project_p.component;

import com.mycompany.project_p.exeption.DBException;
import com.mycompany.project_p.model.Project;
import com.mycompany.project_p.repository.ProjectRepo;
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
@ShellCommandGroup("Project commands")
public class ProjectCommands {
    private ProjectRepo repo;


    @ShellMethod("Create new project (name, description, creator )")
    public String createProject(String name, String description, long creator) {
        Project project = new Project(null, name, creator, description);
        project = repo.save(project);
        return project.toString();
    }

    //    @ShellMethod("Update project")
//    public updateProject(String name, String description, String creator) {
//        Project project = new Project();
//
//    }
    @ShellMethod("Show project details - which users are in which roles")
    public String detailProject(Long id) throws DBException {
        try{
            Project project= repo.findById(id).orElseThrow(() -> new DBException("A project with id " + id + " cannot be found"));


            return "";
        }catch (DBException e) {
            log.error("Cannot find project with id {}", id, e);
            return "The project with id " + id + " cannot be found";
        }

    }

    @ShellMethod("Delete project by ID")
    public String deleteProject(Long id) {
        repo.deleteById(id);
        return "Successfully deleted project with ID " + id;
    }

    @ShellMethod("Get list of projects")
    public String listProject() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .map(Project::toString)
                .collect(Collectors.joining("\n"));
    }
}
