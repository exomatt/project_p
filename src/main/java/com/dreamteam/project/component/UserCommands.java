package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.Role;
import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.AssigmentRepo;
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
import javax.validation.constraints.Null;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Slf4j
@ShellComponent
@ShellCommandGroup("User commands")
public class UserCommands {

    private UserRepo userRepo;
    private ProjectRepo projectRepo;
    private ConfigurationClass configurationClass;
    private AssigmentRepo assigmentRepo;

    @Autowired
    public UserCommands(UserRepo repo, ProjectRepo projectRepo, ConfigurationClass configurationClass, AssigmentRepo assigmentRepo) {
        this.userRepo = repo;
        this.projectRepo = projectRepo;
        this.assigmentRepo=assigmentRepo;
        this.configurationClass=configurationClass;
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
        Role role=null;
        try {
            role = Role.valueOf(roleName);
        }catch(IllegalArgumentException e){
            log.error("Cannot find role {}", roleName, e);
        }
        List<Assigment> assigmentList = assigmentRepo.findByProjectProjectId(projectId);
        for (Assigment assigment: assigmentList) {
            if(assigment.getUser().getUserId()==userID&&role!=null){
                assigment.setRole(role);
                return "User with id "+ userID + " is now a "+roleName+" in project id "+projectId;
            }
        }
        return "Sorry something goes wrong";
    }

    public boolean checkPermission(String methodName){
        String csvFile="UserPermission.csv";
        String csvSplitBy=",";
        String line;
        List<Assigment> assigmentsList = assigmentRepo.findByProjectProjectId(configurationClass.getActualProject().getProjectId());

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] permissions = line.split(csvSplitBy);

                if(assigmentsList==null){
                    for (String actual:permissions) {
                        if(actual.equals("Administrator")&&configurationClass.getUser().getLastName().equals("admin")){
                            return true;
                        }
                    }
                }
                if(permissions[0].equals(methodName)){
                    for (String actual:permissions) {
                        for (Assigment assigment:assigmentsList) {
                            if(actual.equals(assigment.getRole().name())){
                                return true;
                            }
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
