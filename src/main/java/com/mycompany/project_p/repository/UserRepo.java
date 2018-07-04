package com.mycompany.project_p.repository;

import com.mycompany.project_p.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {
    List<User> findByName(String findByName);
    User findByLoginAndPassword(String login, String password);
}
