package com.example.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dto.DtoPer;
import com.example.api.model.Pergunta;
import com.example.api.service.QuizBatchService;
import com.example.api.service.SalvarDb;
import com.example.api.service.ServiceP;
import com.example.api.util.Model;
import com.example.api.util.Model2;

@RestController
@RequestMapping("/api")
public class ControllerApi {
    @Autowired
    private QuizBatchService quizBatchService;
    private final SalvarDb salvarDb;
    private final ServiceP serviceP;
    public ControllerApi(SalvarDb salvarDb, ServiceP serviceP){
        this.salvarDb = salvarDb;
        this.serviceP = serviceP;
    }
    @GetMapping
    public List<Pergunta> eu(){
        return  salvarDb.all();
    }

    @GetMapping("/{id}")
    public Pergunta byId(@PathVariable Long id){
        return salvarDb.id(id);
    }

    @GetMapping("/random")
    public Pergunta random(){
        return salvarDb.aletorio();
    }

    @PostMapping("/reset")
    public String reset() {
        quizBatchService.generateQuizzesInBackground(30);
        return "Reset iniciado. Aguarde alguns segundos e recarregue a página";
    }
    @PostMapping("/delet")
    public void delet(){
        salvarDb.clearAllCaches();
    }

   
}
