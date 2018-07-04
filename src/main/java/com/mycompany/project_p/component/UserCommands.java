package com.mycompany.project_p.component;

import com.mycompany.project_p.exeption.DBException;
import com.mycompany.project_p.model.Project;
import com.mycompany.project_p.model.Role;
import com.mycompany.project_p.model.User;
import com.mycompany.project_p.repository.ProjectRepo;
import com.mycompany.project_p.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;

@Slf4j
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
        return "User created succesfully.";
    }

    @ShellMethod("Add user to role")
    public String addUserToRole(Long userID, Long projectId, String roleName){
        try{
            Project project= projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + projectId + " cannot be found"));
        }
        catch (DBException e){
            log.error("Cannot find project with id {}", projectId, e);
        }
        try{
            User user = userRepo.findById(userID).orElseThrow(()-> new DBException("A user with id " + userID + " cannot be found"));
        }
        catch (DBException e){
            log.error("Cannot find user with id {}", userID, e);
        }
        try {
            if(Role.valueOf(roleName)==null){
                throw new NullPointerException();
            }
        }catch(NullPointerException e){
            log.error("Cannot find role {}", roleName, e);
        }
        //TO DO finish function
        return "";
    }
}
