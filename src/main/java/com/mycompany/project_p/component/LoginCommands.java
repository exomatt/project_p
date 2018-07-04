package com.mycompany.project_p.component;

import com.mycompany.project_p.config.ConfigurationClass;
import com.mycompany.project_p.exeption.DBException;
import com.mycompany.project_p.model.Project;
import com.mycompany.project_p.model.User;
import com.mycompany.project_p.repository.UserRepo;
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

    @Autowired
    private UserRepo userRepo;

    @Autowired
    public LoginCommands(ConfigurationClass configurationClass){
        this.configurationClass= configurationClass;
    }

    @ShellMethod
    public String login(String login, String password){
        try{
            User loggedUser = userRepo.findByLoginAndPassword(login,password);
            if(loggedUser==null){
                throw new DBException("A user with login " + login + " cannot be found");
            }
            configurationClass.setUser(loggedUser);
            return "welcome "+loggedUser.getLastName();
        }catch (DBException e) {
            log.error("Cannot find user with login {}", login, e);
            return "The user with login " + login + " cannot be found";
        }
    }

    @ShellMethod
    public String logout(){
        configurationClass.setUser(null);
        return "logout";
    }
}
