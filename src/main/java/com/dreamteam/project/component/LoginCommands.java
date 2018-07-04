package com.dreamteam.project.component;

import com.dreamteam.project.config.ConfigurationClass;
import com.dreamteam.project.model.User;
import com.dreamteam.project.exeption.DBException;
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

    @Autowired
    public LoginCommands(ConfigurationClass configurationClass, UserRepo userRepo){
        this.configurationClass= configurationClass;
        this.userRepo=userRepo;
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
