package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.crypto.CryptoPassword;
import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Role;
import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.ProjectRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ShellComponent
@ShellCommandGroup("User commands")
public class UserCommands {

    private final UserRepo userRepo;
    private final ProjectRepo projectRepo;
    private final ConfigurationClass configurationClass;
    private final AssigmentRepo assigmentRepo;
    private Map<String, List<String>> permissions = new HashMap<>();

    @ShellMethod("Create new user (lastName, login, password)")
    public String createNewUser(String lastName, String login, String password) {
        CryptoPassword cryptoPassword = new CryptoPassword();
        password = cryptoPassword.encrypt(password);
        if (password.isEmpty())
            return "Problem with encryption";
        User user = new User(null, lastName, login, password);
        user = userRepo.save(user);
        System.out.println(user.toString());
        return "User created succesfully.";
    }

    @ShellMethodAvailability
    public Availability createNewUserAvailavility(){
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Acces denied");
    }

    @ShellMethod("Show users")//Access only for admin
    public void showUsers(){
        List<User> userList = userRepo.findAll();
        for (User user: userList) {
            System.out.println(user.toString());
        }
    }
    @ShellMethodAvailability
    public Availability showUsersAvailavility(){
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Acces denied");
    }

    @ShellMethod("Add user to role (userID, projectID, roleName)")
    public String addUserToRole(Long userID, Long projectId, String roleName) {
        try {
            Role role = Role.valueOf(roleName);
            List<Assigment> assigmentList = assigmentRepo.findByProjectProjectId(projectId);
            for (Assigment assigment : assigmentList) {
                if (assigment.getUser().getUserId() == userID && role != null) {
                    assigment.setRole(role);
                    return "User with id " + userID + " is now a " + roleName + " in project id " + projectId;
                }
            }
            return "Cannot set role " + roleName + " to user " + userID + " in project " + projectId;
        } catch (IllegalArgumentException e) {
            log.error("Cannot find role {}", roleName, e);
            return "Cannot find role";
        }
    }

    @ShellMethodAvailability
    public Availability addUserToRoleAvailavility(){
        if(configurationClass.checkPermission(new Object(){}.getClass().getEnclosingMethod().getName(), permissions)){
            return Availability.available();
        }
        return Availability.unavailable("Acces denied");
    }

    @PostConstruct
    public void loadPermissions() {
        permissions = configurationClass.loadPermissions(this.getClass().getSimpleName());
    }
}
