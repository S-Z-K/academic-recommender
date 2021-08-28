package com.shitajimado.academicwritingrecommender.entities.dtos;

public class DocumentDto {
    private String corpusId;
    private String name;
    private String content;
    private String date;

    public DocumentDto(String corpusId, String name, String content, String date) {
        this.corpusId = corpusId;
        this.name = name;
        this.content = content;
        this.date = date;
    }

    public String getCorpusId() {
        return corpusId;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getDate(){
        return date;
    }
}
