package com.example.api.service;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.api.model.Pergunta;

@Service
public class QuizBatchService {
    private final SalvarDb salvarDb;
    private final ServiceP serviceP;
    
    public QuizBatchService(SalvarDb salvarDb, ServiceP serviceP) {
        this.salvarDb = salvarDb;
        this.serviceP = serviceP;
    }
    
    @Async
    public void generateQuizzesInBackground(int quantidade) {
        try {
            salvarDb.deleteAll();
            
            for (int i = 0; i < quantidade; i++) {
                var mo = serviceP.crateNewQuizz();
                var pt = new Pergunta(mo.getTitle(), mo.getDescription(), 
                                     mo.getAnswer(), mo.getSenioridade());
                salvarDb.salvarDb(pt);
                
                // Pausa curta para evitar rate limit mas não timeoutar
                if ((i + 1) % 10 == 0) {
                    Thread.sleep(3000);
                }
            }
            
            salvarDb.clearAllCaches();
            System.out.println("✅ Gerados " + quantidade + " quizzes!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao gerar quizzes: " + e.getMessage());
        }
    }
}