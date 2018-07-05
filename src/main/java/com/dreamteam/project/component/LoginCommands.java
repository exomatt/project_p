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
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;


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
            System.out.println(login + "  " + password);
            User loggedUser = userRepo.findByLoginAndPassword(login, password);
            if (loggedUser == null) {
                throw new DBException("A user with login " + login + " cannot be found");
            }
            configurationClass.setUser(loggedUser);
            return "welcome " + loggedUser.getLastName();
        } catch (DBException e) {
            log.error("Cannot find user with login {}", login, e);
            return "The user with login " + login + " cannot be found";
        }
    }

    @ShellMethod("User logout")
    public String logout() {
        configurationClass.setUser(null);
        return "logout";
    }

    @ShellMethod("Choose project for user (projectID)")
    public String chooseProject(Long projectId) {
        try {
            Project project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + projectId + " cannot be found"));
            configurationClass.setActualProject(project);
            return "Project: " + project.getProjectName();
        } catch (DBException e) {
            log.error("Cannot find project", projectId, e);
            return "Cannot find project with id " + projectId;
        }
    }

}
