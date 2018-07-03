package com.mycompany.project_p.validiation;

import com.mycompany.project_p.model.User;
import com.mycompany.project_p.repository.UserRepo;

public class UserValidation {
    boolean isValidate = false;
    private UserRepo repo;

    public UserValidation(UserRepo repo) {
        this.repo = repo;
    }

//    public boolean loggin(User user){
//
//    }

}
