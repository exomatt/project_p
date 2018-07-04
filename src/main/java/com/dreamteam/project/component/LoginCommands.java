package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.crypto.CryptoPassword;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.User;
import com.dreamteam.project.exeption.DBException;
import com.dreamteam.project.repository.ProjectRepo;
import com.dreamteam.project.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("Login commands")
public class LoginCommands {

    private ConfigurationClass configurationClass;
    private UserRepo userRepo;
    private ProjectRepo projectRepo;

    public LoginCommands() {
    }

    @Autowired
    public LoginCommands(ConfigurationClass configurationClass, UserRepo userRepo, ProjectRepo projectRepo) {
        this.configurationClass = configurationClass;
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
    }

    @ShellMethod("User login")
    public String login(String login, String password) {
        try {
            CryptoPassword cryptoPassword = new CryptoPassword();
            password = cryptoPassword.encrypt(password);
            if (password.isEmpty())
                return "Problem with encryption";
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

    @ShellMethod("Choose project for user")
    public String chooseProject(Long projectId) {
        Project project = null;
        try {
            project = projectRepo.findById(projectId).orElseThrow(() -> new DBException("A project with id " + projectId + " cannot be found"));
        } catch (DBException e) {
            log.error("Cannot find project", projectId, e);
        }
        configurationClass.setActualProject(project);
        return "Project: " + project.getProjectName();
    }
}
