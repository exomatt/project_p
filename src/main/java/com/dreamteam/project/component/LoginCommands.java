package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.crypto.CryptoPassword;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.User;
import com.dreamteam.project.exeption.DBException;
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


@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ShellComponent
@ShellCommandGroup("Login commands")
public class LoginCommands {

    private final ConfigurationClass configurationClass;
    private final UserRepo userRepo;
    private final ProjectRepo projectRepo;

    @ShellMethod("User login (login, password)")
    public String login(String login, String password) {
        try {
            CryptoPassword cryptoPassword = new CryptoPassword();
            password = cryptoPassword.encrypt(password);
            if (password.isEmpty())
                return "Problem with encryption";
            User loggedUser = userRepo.findByLoginAndPassword(login, password);
            if (loggedUser == null) {
                throw new DBException("Invalid login or password");
            }
            configurationClass.setUser(loggedUser);
            return "welcome " + loggedUser.getLastName();
        } catch (DBException e) {
            log.error("Invalid login or password", login, password, e);
            return e.getMessage();
        }
    }

    @ShellMethodAvailability
    public Availability loginAvailability(){
        if(configurationClass.getUser()!=null){
            return Availability.unavailable("To login you should logout first");
        }
        return Availability.available();
    }

    @ShellMethod("User logout")
    public String logout() {
        if(configurationClass.getUser()==null){
            return "No one is logged";
        }
        String name = configurationClass.getUser().getLastName();
        configurationClass.setUser(null);
        configurationClass.setActualProject(null);
        return "Good bye "+name;
    }

    @ShellMethodAvailability
    public Availability logoutAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        return Availability.available();
    }

    @ShellMethod("Choose project for user (projectID)")
    public String chooseProject(Long projectId) {
        try {
            Project project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + projectId + " cannot be found"));
            configurationClass.setActualProject(project);
            return "Project: " + project.getProjectName();
        } catch (DBException e) {
            log.error("Cannot find project", projectId, e);
            return e.getMessage();
        }
    }

    @ShellMethodAvailability
    public Availability chooseProjectAvailability(){
        if(configurationClass.getUser()==null){
            return Availability.unavailable("No one is logged");
        }
        return Availability.available();
    }
}
