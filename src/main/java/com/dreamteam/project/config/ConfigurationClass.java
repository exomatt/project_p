package com.dreamteam.project.config;

import com.dreamteam.project.model.User;
import com.dreamteam.project.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ConfigurationClass {

    private User user;
    private User admin;
    private UserRepo userRepo;

    @Autowired
    public ConfigurationClass(UserRepo userRepo){
        this.userRepo=userRepo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PostConstruct
    public void createPerson(){
        admin=new User("admin", "admin", "admin");
        userRepo.save(admin);
    }

}
