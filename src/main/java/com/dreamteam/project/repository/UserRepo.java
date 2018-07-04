package com.dreamteam.project.repository;

import com.dreamteam.project.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {
    List<User> findByLastName(String findByName);
    User findByLoginAndPassword(String login, String password);
}
