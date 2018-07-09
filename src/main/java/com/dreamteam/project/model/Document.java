package com.dreamteam.project.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="DOCUMENT")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "DOCUMENT_ID", nullable = false)
    private Long documentId;
    @Column(name = "DOCUMENT_NAME", nullable = false)
    private String documentName;
    @Column(name = "DOCUMENT_DESC", nullable = false)
    private String documentDescription;
    @Column(name = "DOCUMENT_CREATOR", nullable = false)
    private Long creatorId;
    @Column(name = "DOCUMENT_TOPIC", nullable = false)
    private String topic;
    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    private Project project;

    public Document(){}
    public Document(Long id,String documentName, String documentDescription, Long creatorId,String topic,Project project){
        this.documentId=id;
        this.creatorId=creatorId;
        this.documentDescription=documentDescription;
        this.documentName=documentName;
        this.topic=topic;
        this.project=project;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    @Override
    public String toString() {
        return "Document\n" +
                "\tId: " + documentId +
                "\n\tName: '" + documentName + '\'' +
                "\n\tDescription: '" + documentDescription + '\'' +
                "\n\tCreator Id: " + creatorId +
                "\n\tTopic: '" + topic + '\'' +
                "\n\tProject name: " + project.getProjectName();
    }
}
