package com.mycompany.project_p.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Project {
    @Id
    private Long projectId;
    private String projectName;
    private Long creatorId;
    private String projectDescription;
    @OneToMany
    private List<Document> documents = new ArrayList<Document>();


    public Project(String projectName,Long creatorId, String projectDescription, List<Document> documents){
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
