package com.dreamteam.project.repository;

import com.dreamteam.project.model.Assigment;
import com.dreamteam.project.model.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AssigmentRepo extends CrudRepository<Assigment, Long> {
        List<Assigment> findByProjectProjectId (Long projectId);
        List<Assigment> findByUserUserId(Long userId);
}
