package com.mycompany.project_p.component;

import com.mycompany.project_p.config.ConfigurationClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("Login commands")
public class LoginCommands {

    private ConfigurationClass configurationClass;

    @Autowired
    public LoginCommands(ConfigurationClass configurationClass){
        this.configurationClass= configurationClass;
    }

    @ShellMethod
    public String loggin(String login, String password){
        //findUserByLoginAndPassword
        //configurationClass.setUser();
        return "welcome";
    }

    @ShellMethod
    public String logout(){
        return "logout";
    }
}
