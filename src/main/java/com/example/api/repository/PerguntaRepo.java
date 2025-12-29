package com.example.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.Pergunta;
@Repository
public interface PerguntaRepo extends JpaRepository<Pergunta, Long> {
    @Query(value = "SELECT * FROM pergunta ORDER BY RANDOM() LIMIT 1", nativeQuery = true)

    Pergunta findRandom(); 
}
