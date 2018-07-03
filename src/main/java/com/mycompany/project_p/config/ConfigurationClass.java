package com.mycompany.project_p.config;

import com.mycompany.project_p.model.User;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ConfigurationClass {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PostConstruct
    public void createPerson(){
        user=new User("admin", "admin", "admin");
    }
}
