package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.crypto.CryptoPassword;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.Role;
import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.ProjectRepo;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@ShellComponent
@ShellCommandGroup("User commands")
public class UserCommands {

    private UserRepo userRepo;
    private ProjectRepo projectRepo;
    private ConfigurationClass configurationClass;

    @Autowired
    public UserCommands(UserRepo repo, ProjectRepo projectRepo, ConfigurationClass configurationClass) {
        this.userRepo = repo;
        this.projectRepo = projectRepo;
        this.configurationClass = configurationClass;
    }

    @ShellMethod("Create new user")
    public String createNewUser(String lastName, String login, String password) {
        CryptoPassword cryptoPassword = new CryptoPassword();
        password = cryptoPassword.encrypt(password);
        if (password.isEmpty())
            return "Problem with encryption. User not created";
        User user = new User(lastName, login, password);
        user = userRepo.save(user);
        System.out.println(user.toString());
        return "User created succesfully.";
    }


    @ShellMethod("Add user to role")
    public String addUserToRole(Long userID, Long projectId, String roleName) {
        try {
            Project project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + projectId + " cannot be found"));
            User user = userRepo.findById(userID).orElseThrow(() -> new DBException("A user with id " + userID + " cannot be found"));
        } catch (DBException e) {
            log.error("Cannot find user or project", userID, projectId, e);
        }
        try {
            if (Role.valueOf(roleName) == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            log.error("Cannot find role {}", roleName, e);
        }
        //TODO finish function
        return "";
    }

    public boolean checkPermission(String methodName) {
        String csvFile = "UserPermission.csv";
        String csvSplitBy = ",";
        String line;
        User loggedUser = configurationClass.getUser();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {


            while ((line = br.readLine()) != null) {

                String[] permissions = line.split(csvSplitBy);

                if (permissions[0].equals(methodName)) {
                    for (String actual : permissions) {
                        if (actual.equals("")) {
                            return true;
                            //TODO End if, check actual with user role in project
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("File not found", e);
        }
        return false;
    }
}
