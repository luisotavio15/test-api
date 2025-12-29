package com.example.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.api.model.Pergunta;
import com.example.api.repository.PerguntaRepo;

@Service
public class SalvarDb {
    @Autowired
    private  PerguntaRepo perguntaRepo;
    

    @Autowired
    private CacheManager cacheManager;

    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            cacheManager.getCache(name).clear();
        });
    }


    
    @CachePut(value = "perguntas", key = "#p.id")
    public Pergunta salvarDb(Pergunta p){
        return perguntaRepo.save(p);
    }
    @Cacheable("all")
    public List<Pergunta> all(){
        return perguntaRepo.findAll();
    }

    @Cacheable("id")
    public Pergunta id(Long id){
        return perguntaRepo.findById(id).orElseThrow( () -> new RuntimeException("nao encontrado"));
    }

    public Pergunta aletorio(){
        return perguntaRepo.findRandom();
    }

    public void deleteAll(){
        perguntaRepo.deleteAllInBatch();
    }

    public void deleteById(Long id){
        perguntaRepo.deleteById(id);
    }
    
}
