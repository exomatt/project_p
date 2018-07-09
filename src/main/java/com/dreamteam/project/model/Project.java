package com.dreamteam.project.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "PROJECT")
public class Project {
    @Id
    @GeneratedValue
    @Column(name = "PROJECT_ID", nullable = false)
    private Long projectId;
    @Column(name = "PROJECT_NAME", nullable = false)
    private String projectName;
    @Column(name = "PROJECT_CREATOR", nullable = false)
    private Long creatorId;
    @Column(name = "PROJECT_DESC", nullable = false)
    private String projectDescription;
    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();

    public Project() {

    }

    public Project(Long id, String projectName, Long creatorId, String projectDescription) {
        this.projectName = projectName;
        this.creatorId = creatorId;
        this.projectDescription = projectDescription;
    }

    public Project(Long id, String projectName, Long creatorId, String projectDescription, List<Document> documents) {
        this.projectId = id;
        this.projectName = projectName;
        this.creatorId = creatorId;
        this.projectDescription = projectDescription;
        this.documents = documents;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
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

    public void addToList(Document document) {
        documents.add(document);
    }

    public String toStringShort() {
        return "Project\n" +
                "\tId: " + projectId +
                "\n\tName: '" + projectName + '\'' +
                "\n\tCreator Id: " + creatorId +
                "\n\tDescription: '" + projectDescription + '\'' +
                '\n';
    }

    @Override
    public String toString() {
        return "Project\n" +
                "\tId: " + projectId +
                "\n\tName: '" + projectName + '\'' +
                "\n\tCreator Id: " + creatorId +
                "\n\tDescription: '" + projectDescription + '\'' +
                "\n\tDocuments: \n" + documents +
                '\n';
    }
}
