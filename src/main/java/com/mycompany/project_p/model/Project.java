package com.mycompany.project_p.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Project {
    @Id
    @GeneratedValue
    private Long projectId;
    private String projectName;
    private Long creatorId;
    private String projectDescription;
    @OneToMany
    private List<Document> documents = new ArrayList<Document>();

    public Project(){}
    public Project(Long id ,String projectName,Long creatorId, String projectDescription, List<Document> documents){
        this.projectId=id;
        this.projectName=projectName;
        this.creatorId=creatorId;
        this.projectDescription=projectDescription;
        this.documents=documents;
    }
    public Project(String projectName,Long creatorId, String projectDescription){
        this.projectName=projectName;
        this.creatorId=creatorId;
        this.projectDescription=projectDescription;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + projectName + '\'' +", description='" + projectDescription+ '\'' +", creator id='" + creatorId + '\'' +
                ", documentlist='" + documents.toString() + '\'' +
                '}';//need to check if correct
    }
}
