package com.example.api.controller;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dto.DtoPer;
import com.example.api.model.Pergunta;
import com.example.api.service.SalvarDb;
import com.example.api.service.ServiceP;
import com.example.api.util.Model;
import com.example.api.util.Model2;

@RestController
@RequestMapping("/api")
public class ControllerApi {
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
    public void reset() {
        salvarDb.deleteAll();

        for (int i = 0; i < 10; i++) {
            // gera quiz
            Model2 mo = serviceP.crateNewQuizz();
            Pergunta pt = new Pergunta(mo.getTitle(), mo.getDescription(), mo.getAnswer(), mo.getSenioridade());
            salvarDb.salvarDb(pt);

            // a cada 5 quizzes, pausa 1 minuto
            if ((i + 1) %  8 == 0) {
                try {
                    Thread.sleep(60_000); // 60 segundos = 1 minuto
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Erro ao pausar entre lotes", e);
                }
            }
        }
        salvarDb.clearAllCaches();
    }
    @PostMapping("/delet")
    public void delet(){
        salvarDb.clearAllCaches();
    }

   
}
