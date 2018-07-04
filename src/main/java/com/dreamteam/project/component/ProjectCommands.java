package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.ProjectRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.PostConstruct;
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
//@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ShellComponent
@ShellCommandGroup("Project commands")
public class ProjectCommands {
    private final ProjectRepo projectRepo;
    private final AssigmentRepo assigmentRepo;
    private final ConfigurationClass configurationClass;
    private final UserRepo userRepo;
    private Map<String, List<String>> permissions = new HashMap<>();

    @ShellMethod("Create new project (name, description, creator )")
    public String createProject(String name, String description, long creator) {
        Project project = new Project(null, name, creator, description);
        project = projectRepo.save(project);
        return project.toString();
    }

    @ShellMethod("Update project (id, name, description, creator )")
    public String updateProject(@ShellOption(defaultValue = "-1") String id, @ShellOption(defaultValue = "") String name, @ShellOption(defaultValue = "") String description, String creator) throws DBException {
        try {
            Long projectId = Long.parseLong(id);
            Project project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + id + " cannot be found"));
            Long creatorId = Long.parseLong(creator);
            if (creatorId != -1) {
                userRepo.findById(creatorId).orElseThrow(() -> new DBException("A user  with id " + creator + " cannot be found"));
                project.setCreatorId(creatorId);
            }
            if (!name.isEmpty())
                project.setProjectName(name);
            if (!description.isEmpty())
                project.setProjectDescription(description);
            project = projectRepo.save(project);
            return project.toString();
        } catch (NumberFormatException exn) {
            log.error("Error in parsing id", exn);
            return "Wrong id format";
        }
    }

    @ShellMethod("Show project details - which users are in which roles")
    public String detailProject() {
        Project project = configurationClass.getActualProject();
        return assigmentRepo.findByProjectProjectId(project.getProjectId()).stream()
                .map(Assigment::toString)
                .collect(Collectors.joining("\n"));
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

    @PostConstruct
    public void loadPermission() {
        String csvFile = "ProjectPermission.csv";
        String csvSplitBy = ",";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                List<String> roles = new ArrayList<>();
                String[] permission = line.split(csvSplitBy);

                for (int i = 1; i < permission.length; i++) {
                    roles.add(permission[i]);
                }
                permissions.put(permission[0], roles);
            }
            System.out.println(permissions);
        } catch (IOException e) {
            log.error("File not found", e);
        }
    }
}
