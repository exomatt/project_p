package com.mycompany.project_p.component;

import com.mycompany.project_p.model.Project;
import com.mycompany.project_p.model.User;
import com.mycompany.project_p.repository.ProjectRepo;
import com.mycompany.project_p.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;

@ShellComponent
@ShellCommandGroup("User commands")
public class UserCommands {

    private UserRepo repo;
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    public UserCommands(UserRepo repo) {
        this.repo = repo;
    }

    @ShellMethod
    public String createNewUser(String lastName, String login, String password){
        User user = new User(lastName, login, password);
        user = repo.save(user);
        System.out.println(user.toString());
        return "Project created succesfully.";
    }

    @ShellMethod
    public String addUserToRole(Long userID, Long projectId, String roleName){
        Optional<Project> project = projectRepo.findById(projectId);
        Optional<User> user = repo.findById(userID);
        //TO DO finish function
        return "";
    }
}
