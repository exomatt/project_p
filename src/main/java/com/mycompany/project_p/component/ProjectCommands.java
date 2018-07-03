package com.mycompany.project_p.component;

import com.mycompany.project_p.model.Project;
import com.mycompany.project_p.repository.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("Project commands")
public class ProjectCommands {
    private ProjectRepo repo;

    @Autowired
    public ProjectCommands(ProjectRepo repo) {
        this.repo = repo;
    }

    @ShellMethod
    public createProject(String name, String description, String creator){
        Project project = new Project(null, description, creator);
        project = repo.save(project);
    }
}
