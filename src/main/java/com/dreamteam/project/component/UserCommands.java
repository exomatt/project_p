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
import java.util.stream.Collectors;

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

    @ShellMethod("Show users")
    public String showUsers() {
        return userRepo.findAll().stream()
                .map(User::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethodAvailability
    public Availability showUsersAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.checkPermission(new Object(){
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
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Delete user role in project")
    public String deleteUserRole(String userLogin, String roleName) {
        try {
            Role role = Role.valueOf(roleName);
            Project project = configurationClass.getActualProject();
            if (project == null)
                return "Project is not choosen";
            User user = userRepo.findByLogin(userLogin);
            if (user == null)
                return "User with that login dont exist";
            Long userID = user.getUserId();
            Assigment assigment = assigmentRepo.findByUserUserIdAndProjectProjectIdAndRole(userID, project.getProjectId(), role);
            if (assigment==null)
                return "Cannot delete role " + roleName + " to user " + userID + " in project " + project.getProjectId();
            assigmentRepo.delete(assigment);
            return "User deleted from " + assigment.toString();
        } catch (IllegalArgumentException e) {
            log.error("Cannot find role {}", roleName, e);
            return "Cannot find role";
        }
    }

    @ShellMethodAvailability
    public Availability deleteUserRoleAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.getActualProject() == null) {
            return Availability.unavailable("Choose project");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @ShellMethod("Update user role(userId, projectId, roleName, newRoleName)")
    public String updateUserRole(@ShellOption(defaultValue = "-1") Long id, @ShellOption(defaultValue = "-1") Long projectId, @ShellOption(defaultValue = "") String role, @ShellOption(defaultValue = "") String newRole) throws DBException {

        try {
            if (!role.isEmpty() && !newRole.isEmpty()) {
                Role newSettingRole = Role.valueOf(newRole);
                Role currentRole = Role.valueOf(role);
                Assigment assigment = assigmentRepo.findByUserUserIdAndProjectProjectIdAndRole(id, projectId, currentRole);
                Assigment assigment2 = assigmentRepo.findByUserUserIdAndProjectProjectIdAndRole(id, projectId, newSettingRole);
                if (assigment2 != null) {
                    return "This role of user in project already exist";
                } else {

                    assigment.setRole(newSettingRole);
                    assigment = assigmentRepo.save(assigment);
                    return assigment.toString();
                }
            } else {
                return "Wrong parameters";
            }

        } catch (IllegalArgumentException e) {
            log.error("Connection between this user and project not exist{}", e);
            return e.getMessage();
        }
    }

    @ShellMethodAvailability
    public Availability updateUserRoleAvailability() {
        if (configurationClass.getUser() == null) {
            return Availability.unavailable("No one is logged");
        }
        if (configurationClass.checkPermission(new Object() {
        }.getClass().getEnclosingMethod().getName(), permissions)) {
            return Availability.available();
        }
        return Availability.unavailable("Access denied");
    }

    @PostConstruct
    public void loadPermissions() {
        permissions = configurationClass.loadPermissions(this.getClass().getSimpleName());
        if(permissions.isEmpty()){
            log.error("User permissions are not ready to use");
        }
    }
}
