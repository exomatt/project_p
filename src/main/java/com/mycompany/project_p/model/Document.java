package com.mycompany.project_p.model;

public class Document {
    private Long documentId;
    private String documentName;
    private String documentDescription;
    private Long creatorId;
    private String topic;

    public Document(){}
    public Document(String documentName, String documentDescription, Long creatorId,String topic){
        this.creatorId=creatorId;
        this.documentDescription=documentDescription;
        this.documentName=documentName;
        this.topic=topic;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }
}
