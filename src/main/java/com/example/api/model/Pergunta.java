package com.example.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pergunta")

public class Pergunta implements Serializable{
    
    private static final long serialVersionUID = 1L; 
    public Pergunta(String title, String descri, String a, String senioridade){
        this.senioridade = senioridade;
        this.answer = a;
        this.title = title;
        this.description  = descri;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String answer;
    private String senioridade;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public String getSenioridade() {
        return senioridade;
    }
    public void setSenioridade(String senioridade) {
        this.senioridade = senioridade;
    }
    
}
