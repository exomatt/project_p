package com.mycompany.project_p.repository;

import com.mycompany.project_p.model.Assigment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AssigmentRepo extends CrudRepository<Assigment, Long> {
        List<Assigment> fingByProject(Long id);
}
