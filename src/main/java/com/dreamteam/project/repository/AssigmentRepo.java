package com.dreamteam.project.repository;

import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Project;
import com.dreamteam.project.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssigmentRepo extends CrudRepository<Assigment, Long> {
    List<Assigment> findByProjectProjectId(Long projectId);

    List<Assigment> findByUserUserId(Long userId);

    Assigment findByUserUserIdAndProjectProjectIdAndRole(Long userId, Long projectId, Role role);
}
