package com.dreamteam.project.config;

import com.dreamteam.project.model.Project;
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
    private Project actualProject;

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

    public Project getActualProject() {
        return actualProject;
    }

    public void setActualProject(Project actualProject) {
        this.actualProject = actualProject;
    }

    @PostConstruct
    public void createUser(){
        admin=new User("admin", "admin", "admin");
        userRepo.save(admin);
    }
}
