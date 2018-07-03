package com.mycompany.project_p.model;

import javax.annotation.Generated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class Document {
    @Id
    @Generated
    private Long documentId;
    private String documentName;
    private String documentDescription;
    private Long creatorId;
    private String topic;
    @ManyToOne
    private Project project;

    public Document(){}
    public Document(String documentName, String documentDescription, Long creatorId,String topic,Project project){
        this.creatorId=creatorId;
        this.documentDescription=documentDescription;
        this.documentName=documentName;
        this.topic=topic;
        this.project=project;
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
    public String toString() {
        return "Project{" +
                "name='" + projectName + '\'' +", description='" + projectDescription+ '\'' +", creator id='" + creatorId + '\'' +
                ", documentlist='" + documents.toString() + '\'' +
                '}';//need to check if correct
    }
}
