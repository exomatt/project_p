package com.dreamteam.project.config;

import com.dreamteam.project.crypto.CryptoPassword;
import com.dreamteam.project.model.Assigment;
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
    public void createUser(){
        String password;
        CryptoPassword cryptoPassword = new CryptoPassword();
        password = cryptoPassword.encrypt("admin");
        if (password.isEmpty())
            throw new RuntimeException();
        //TODO Something better than this exeption!! maybe own exeption? / just for demo
        if(userRepo.findByLoginAndPassword("admin",password)==null){
            admin=new User("admin", "admin", password);
            userRepo.save(admin);
        }
    }

    public boolean checkPermission(String methodName, Map<String, List<String>> permissions) {
        methodName = methodName.replace("Availability","");
        System.out.println(methodName);
        if(user.getLogin().equals(admin.getLogin())){
            return true;
        }
        //TODO fix (in sytuation when assigment is not created)
        List<Assigment> assigmentList = assigmentRepo.findByUserUserId(user.getUserId());
        if(user!=null&&assigmentList!=null){
            for (Map.Entry<String, List<String>> entry : permissions.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                if(methodName.equals(key)){
                    for (Assigment assigment : assigmentList) {
                        if(assigment.getProject()==actualProject){
                            for (String ok : values) {
                                System.out.println(assigment.getRole().name());
                                if(ok.equals(assigment.getRole().name())){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public Map<String, List<String>> loadPermissions(String className) {
        Map<String, List<String>> permissions = new HashMap<>();
        String csvFile = className.replace("Commands", "Permission.csv");
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
            return permissions;
        } catch (IOException e) {
            log.error("File not found", e);
            return permissions;
        }
    }
}
