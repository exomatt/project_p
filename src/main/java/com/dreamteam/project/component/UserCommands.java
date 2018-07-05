package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.crypto.CryptoPassword;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.Role;
import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.AssigmentRepo;
import com.dreamteam.project.repository.ProjectRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import javax.annotation.PostConstruct;
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
    public Availability createNewUserAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Show users")//Access only for admin
    public void showUsers() {
        List<User> userList = userRepo.findAll();
        for (User user : userList) {
            System.out.println(user.toString());
        }
    }

    @ShellMethodAvailability
    public Availability showUsersAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Add user to role (userLogin, roleName)")
    public String addUserToProject(String userLogin, String roleName) {
        try {
            Role role = Role.valueOf(roleName);
            Project project = configurationClass.getActualProject();
            if (project == null)
                return "Project is not choosen";
            User user = userRepo.findByLogin(userLogin);
            if (user == null)
                return "User with that login dont exist";
            Long userID = user.getUserId();
            Assigment assigments = assigmentRepo.findByUserUserIdAndProjectProjectIdAndRole(userID, project.getProjectId(), role);
            if (assigments != null)
                return "Cannot set role " + roleName + " to user " + userID + " in project " + project.getProjectId();
            Assigment assigment = new Assigment(user, role, project);
            assigmentRepo.save(assigment);
            return "User add to  " + assigment.toString();
        } catch (IllegalArgumentException e) {
            log.error("Cannot find role {}", roleName, e);
            return "Cannot find role";
        }
    }

    @ShellMethodAvailability
    public Availability addUserToProjectAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Update user role(userId, projectId, roleName, newRoleName)")
    public String updateUserRole(@ShellOption(defaultValue = "-1") String userId, @ShellOption(defaultValue = "-1") String projectId, @ShellOption(defaultValue = "") String role, @ShellOption(defaultValue = "") String newRole) throws DBException {
        Long id = Long.parseLong(userId);
        Long thisProjectId = Long.parseLong(projectId);
        try {
            if (role != null && newRole != null) {
                Role newwRole = Role.valueOf(newRole);
                Role currentRole = Role.valueOf(role);
                Assigment assigment = assigmentRepo.findByUserUserIdAndProjectProjectIdAndRole(id, thisProjectId, currentRole);
                Assigment assigment2 = assigmentRepo.findByUserUserIdAndProjectProjectIdAndRole(id, thisProjectId, newwRole);
                if (assigment2 != null) {
                    return "This role of user in project already exist";
                } else {

                    assigment.setRole(newwRole);
                    assigment = assigmentRepo.save(assigment);
                    return "Role updated.";
                }
            } else {
                return "Wrong parameters of roles";
            }

        } catch (IllegalArgumentException e) {
            log.error("Connection between this user and project not exist{}", e);
            return "User with project not found";
        }//need testes!!
    }

    @PostConstruct
    public void loadPermissions() {
        permissions = configurationClass.loadPermissions(this.getClass().getSimpleName());
    }
}
