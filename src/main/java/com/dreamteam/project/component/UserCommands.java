package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Role;
import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.ProjectRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

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

    @ShellMethod("Create new user")
    public String createNewUser(String lastName, String login, String password) {
        System.out.println(checkPermission(new Object(){}.getClass().getEnclosingMethod().getName()));
        User user = new User(lastName, login, password);
        user = userRepo.save(user);
        System.out.println(user.toString());
        return "User created succesfully.";
    }

    @ShellMethod("Add user to role")
    public String addUserToRole(Long userID, Long projectId, String roleName) {
        Role role = null;
        try {
            role = Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            log.error("Cannot find role {}", roleName, e);
            return "Cannot find role";
        }
        List<Assigment> assigmentList = assigmentRepo.findByProjectProjectId(projectId);
        for (Assigment assigment : assigmentList) {
            if (assigment.getUser().getUserId() == userID && role != null) {
                assigment.setRole(role);
                return "User with id " + userID + " is now a " + roleName + " in project id " + projectId;
            }
        }
        return "Cannot set role " + roleName + " to user "+userID+" in project " + projectId;
    }

    public boolean checkPermission(String methodName) {
        User loggedUser = configurationClass.getUser();
        if("admin".equals(loggedUser.getLogin())){
            return true;
        }
        //TODO fix (in sytuation when assigment is not created)
        List<Assigment> assigmentList = assigmentRepo.findByUserUserId(loggedUser.getUserId());
        if(loggedUser!=null){
            for (Map.Entry<String, List<String>> entry : permissions.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                if(methodName.equals(key)){
                    for (Assigment assigment : assigmentList) {
                        if(assigment.getProject()==configurationClass.getActualProject()){
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

    @PostConstruct
    public void loadPermission() {
        String csvFile = "UserPermission.csv";
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
