package com.example.api.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.api.model.Pergunta;
import com.example.api.repository.PerguntaRepo;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Service 
public class QuizzVerific {
    
    @Autowired
    private PerguntaRepo perguntaRepo;
    
    private final ChatModel ia;
    
    public QuizzVerific() {
        this.ia = OpenAiChatModel.builder()
                .apiKey(System.getenv("API_KEY")) 
                .baseUrl("https://api.groq.com/openai/v1") 
                .modelName("llama-3.1-8b-instant")               
                .build();
    }
    
    public boolean verific(Pergunta pergunta, String respostaUsuario) {
        
        if (pergunta == null || respostaUsuario == null || respostaUsuario.trim().isEmpty()) {
            return false;
        }
        
        String respostaCorreta = pergunta.getAnswer();
        if (respostaCorreta == null || respostaCorreta.trim().isEmpty()) {
            return false;
        }
        
        
        String prompt = criarPrompt(pergunta.getDescription(), respostaUsuario, respostaCorreta);
        
        try {
            String respostaIA = ia.chat(prompt);
            System.out.println("Resposta IA: " + respostaIA);
            System.out.println(prompt);

            return parsearRespostaIA(respostaIA);
        } catch (Exception e) {
            
            return verificarLocalmente(respostaUsuario, respostaCorreta);
        }
    }
    
    private String criarPrompt(String pergunta, String respostaUsuario, String respostaCorreta) {
        return """
            Você é um assistente especializado em verificar respostas de quizzes de programação.
            
            PERGUNTA: %s
            
            RESPOSTA DO USUÁRIO: %s
            
            
            
            Instruções:
            1. Analise se a resposta do usuário está correta ou não.
            2. Considere sinônimos, diferentes formas de escrever a mesma coisa, e respostas parciais corretas.
            3. Para respostas de código, ignore diferenças de espaçamento, quebras de linha e nomes de variáveis.
            4. Retorne APENAS uma das seguintes palavras:
               - "true" se a resposta estiver essencialmente correta
               - "false" se a resposta estiver incorreta
            
            Não inclua explicações, comentários ou texto adicional.
            """.formatted(pergunta, respostaUsuario);
    }
    
    private boolean parsearRespostaIA(String respostaIA) {
        if (respostaIA == null) return false;
        
        String respostaLimpa = respostaIA.trim().toLowerCase();
        
        
        respostaLimpa = respostaLimpa.replaceAll("[^a-z]", "");
        
        return respostaLimpa.equals("true") || 
               respostaLimpa.equals("verdadeiro") || 
               respostaLimpa.equals("sim") || 
               respostaLimpa.equals("yes") || 
               respostaLimpa.equals("correct") || 
               respostaLimpa.equals("correto");
    }
    
    private boolean verificarLocalmente(String respostaUsuario, String respostaCorreta) {
        
        String usuario = normalizar(respostaUsuario);
        String correta = normalizar(respostaCorreta);
        
        
        if (usuario.equals(correta)) {
            return true;
        }
        
        
        if (usuario.contains(correta) || correta.contains(usuario)) {
            return true;
        }
        
        
        String[] palavrasCorretas = correta.split("\\s+");
        String[] palavrasUsuario = usuario.split("\\s+");
        
        int palavrasCorrespondentes = 0;
        for (String palavraCorreta : palavrasCorretas) {
            if (palavraCorreta.length() > 2) { 
                for (String palavraUsuario : palavrasUsuario) {
                    if (palavraUsuario.contains(palavraCorreta) || 
                        palavraCorreta.contains(palavraUsuario)) {
                        palavrasCorrespondentes++;
                        break;
                    }
                }
            }
        }
        
       
        return (double) palavrasCorrespondentes / palavrasCorretas.length >= 0.5;
    }
    
    private String normalizar(String texto) {
        if (texto == null) return "";
        
        return texto.toLowerCase()
                   .replaceAll("[áàãâä]", "a")
                   .replaceAll("[éèêë]", "e")
                   .replaceAll("[íìîï]", "i")
                   .replaceAll("[óòõôö]", "o")
                   .replaceAll("[úùûü]", "u")
                   .replaceAll("[ç]", "c")
                   .replaceAll("[^a-z0-9\\s]", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    
    public boolean verificarPorId(Long perguntaId, String respostaUsuario) {
        Pergunta pergunta = perguntaRepo.findById(perguntaId)
                .orElseThrow(() -> new RuntimeException("Pergunta não encontrada"));
        
        return verific(pergunta, respostaUsuario);
    }

}