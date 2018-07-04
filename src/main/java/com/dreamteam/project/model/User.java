package com.dreamteam.project.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class User {
    @Id
    @GeneratedValue
    private Long userId;
    private String lastName;
    private String login;
    private String password;

    public User(){}

    public User(String lastName,String login, String password){
        this.lastName=lastName;
        this.login=login;
        this.password=password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }
    @Override
    public String toString() {
        return "User{" +
                "name='" + lastName + '\'' +", login='" + login+ '\''+
                '}';//need to check if correct
    }
}
