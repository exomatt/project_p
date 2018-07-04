package com.dreamteam.project.repository;

import com.dreamteam.project.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends CrudRepository<Project, Long > {
    Project findByName(String name);
}
