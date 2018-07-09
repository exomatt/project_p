package com.dreamteam.project.config;

import com.dreamteam.project.crypto.CryptoPassword;
import com.dreamteam.project.exeption.ApplicationException;
import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Document;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class ConfigurationClass {

    private User user;
    private User admin;
    private Project actualProject;
    private UserRepo userRepo;
    private AssigmentRepo assigmentRepo;

    @Autowired
    public ConfigurationClass(UserRepo userRepo, AssigmentRepo assigmentRepo){
        this.userRepo=userRepo;
        this.assigmentRepo=assigmentRepo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getActualProject() {
        return actualProject;
    }

    public void setActualProject(Project actualProject) {
        this.actualProject = actualProject;
    }

    @PostConstruct
    public void  createUser() throws ApplicationException {
        try {
            user = null;
            String password;
            CryptoPassword cryptoPassword = new CryptoPassword();
            password = cryptoPassword.encrypt("admin");
            if (password.isEmpty())
                throw new ApplicationException("A problem occurred while attempting to run the application");
            if (userRepo.findByLoginAndPassword("admin", password) == null) {
                admin = new User("Administrator", "admin", password);
                userRepo.save(admin);
            } else {
                admin = userRepo.findByLoginAndPassword("admin", password);
            }
            //return "";
        } catch (ApplicationException exception) {
            //return exception.getMessage();
        }

    }

    public boolean checkCreator(String methodName, Map<String, List<String>> permissions){
        methodName = methodName.replace("Availability","");
        List<Assigment> assigmentList = assigmentRepo.findByUserUserId(user.getUserId());
        if(assigmentList!=null){
            for (Map.Entry<String, List<String>> entry : permissions.entrySet()) {
                String key = entry.getKey();
                List<String> roles = entry.getValue();

                if(methodName.equals(key)){
                    for (Assigment assigment : assigmentList) {
                        if(assigment.getProject().getProjectId().equals(actualProject.getProjectId())){
                            for (String role : roles) {
                                for (Document doc: assigment.getProject().getDocuments()) {
                                    if(user.getUserId().equals(doc.getCreatorId())){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean checkPermission(String methodName, Map<String, List<String>> permissions) {
        methodName = methodName.replace("Availability","");
        if(user.getLastName().equals(admin.getLastName())){
            for (Map.Entry<String, List<String>> entry : permissions.entrySet()) {
                String key = entry.getKey();
                List<String> rolesList = entry.getValue();

                if(methodName.equals(key)){
                    for (String role : rolesList) {
                        if(role.equals("Administrator")){
                            return true;
                        }
                    }
                }
            }
        }
        List<Assigment> assigmentList = assigmentRepo.findByUserUserId(user.getUserId());
        if(assigmentList!=null){
            for (Map.Entry<String, List<String>> entry : permissions.entrySet()) {
                String key = entry.getKey();
                List<String> roles = entry.getValue();

                if(methodName.equals(key)){
                    for (Assigment assigment : assigmentList) {
                        if(actualProject==null){
                            for (String role : roles) {
                                if(role.equals(assigment.getRole().name())){
                                    return true;
                                }
                            }
                        }
                        else{
                            if(assigment.getProject().getProjectId().equals(actualProject.getProjectId())){
                                for (String role : roles) {
                                    if(role.equals(assigment.getRole().name())){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
