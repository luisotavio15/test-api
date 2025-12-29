package com.example.api.util;

public class Model2 {
    
    private String title; 
    private String description;
    private String answer;
    private String senioridade;

    public Model2(String title, String description, String answer, String senioridade) {
        this.title = title;
        this.description = description;
        this.answer = answer;
        this.senioridade = senioridade;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public String getSenioridade() {
        return senioridade;
    }
    public void setSenioridade(String senioridade) {
        this.senioridade = senioridade;
    }
}
