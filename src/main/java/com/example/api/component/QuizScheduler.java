package com.example.api.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.api.model.Pergunta;
import com.example.api.service.SalvarDb;
import com.example.api.service.ServiceP;
import com.example.api.util.Model;
import com.example.api.util.Model2;



@Component
public class QuizScheduler {
    private final SalvarDb salvarDb;
    private final ServiceP serviceP;

    public QuizScheduler(SalvarDb salvarDb, ServiceP serviceP) {
        this.salvarDb = salvarDb;
        this.serviceP = serviceP;
    }

    @Scheduled(cron = "0 0 0 */7 * *")
    public void reset() {
        salvarDb.deleteAll();

        for (int i = 0; i < 50; i++) {
            // gera quiz
            Model2 mo = serviceP.crateNewQuizz();
            Pergunta pt = new Pergunta(mo.getTitle(), mo.getDescription(), mo.getAnswer(), mo.getSenioridade());
            salvarDb.salvarDb(pt);

            // a cada 5 quizzes, pausa 1 minuto
            if ((i + 1) % 5 == 0) {
                try {
                    Thread.sleep(60_000); // 60 segundos = 1 minuto
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Erro ao pausar entre lotes", e);
                }
            }
        }
    }}

