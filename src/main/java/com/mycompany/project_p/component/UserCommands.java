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

    private UserRepo userRepo;
    private ProjectRepo projectRepo;

    @Autowired
    public UserCommands(UserRepo repo, ProjectRepo projectRepo) {
        this.userRepo = repo;
        this.projectRepo = projectRepo;
    }

    @ShellMethod("Create new user")
    public String createNewUser(String lastName, String login, String password){
        User user = new User(lastName, login, password);
        user = userRepo.save(user);
        System.out.println(user.toString());
        return "Project created succesfully.";
    }

    @ShellMethod("Add user to role")
    public String addUserToRole(Long userID, Long projectId, String roleName){
        Optional<Project> project = projectRepo.findById(projectId);
        Optional<User> user = userRepo.findById(userID);
        //TO DO finish function
        return "";
    }
}
