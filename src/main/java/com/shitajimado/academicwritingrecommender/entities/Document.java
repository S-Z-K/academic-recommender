package com.shitajimado.academicwritingrecommender.entities;

import com.shitajimado.academicwritingrecommender.entities.dtos.DocumentDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.shitajimado.academicwritingrecommender.core.exceptions.DocumentNotCreatedException;
import org.springframework.data.annotation.Id;

public class Document {
    @Id
    private String id;
    private String corpusId;
    private String name;
    private String textId;
    private String date;

    public Document() {

    }

    public Document(String corpusId, String name, String date) {
        this.corpusId = corpusId;
        this.name = name;
        this.date = date;
    }

    public Document(String name, String content, Text annotatedText, String date) throws DocumentNotCreatedException {
        this.corpusId = "";
        this.name = name;
        this.textId = annotatedText.getId();
        this.date = date;
    }

    public Document(DocumentDto documentDto) {
        this(documentDto.getCorpusId(), documentDto.getName(), documentDto.getDate());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getCorpusId() {
        return corpusId;
    }

    public void setCorpusId(String corpusId) {
        this.corpusId = corpusId;
    }

    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
