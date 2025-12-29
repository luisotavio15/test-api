package com.example.api.service;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.api.util.Model;
import com.example.api.util.Model2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Service
public class ServiceP {
    private final Random random;
    private final ChatModel ia;
    private final ObjectMapper objectMapper;

    private final String[] lin = { "Java", "Cobol", "JavaScript", "Pawn", "C++", "C", "Python" };
    private final String[] dif = { "estagiario", "junior", "pleno", "senior", 
                                  "Engenharia de Software", "arquiteto de software" };
    
    // Regex para extrair JSON de blocos de código
    private static final Pattern JSON_PATTERN = Pattern.compile(
        "\\{(?:[^{}]|\\{(?:[^{}]|\\{[^{}]*\\})*\\})*\\}", 
        Pattern.DOTALL
    );

    public ServiceP() {
        random = new Random();
        objectMapper = new ObjectMapper();
        
        ia = OpenAiChatModel.builder()
                .apiKey(System.getenv("API_KEY")) 
                .baseUrl("https://api.groq.com/openai/v1") 
                .modelName("llama-3.1-8b-instant") 
                              
                .build();
    }

    public Model2 crateNewQuizz() {
        String l = createL();
        String d = createD();
        
        String prompt = """
Você é uma inteligência artificial especializada em criar quizzes de programação.
Gere exatamente UM quiz em formato JSON válido.

Formato obrigatório:
{
  "title": "Título curto e claro sobre o tema",
  "description": "Pergunta objetiva sobre o tema",
  "answer": "Resposta direta e correta"
}

Regras:
1. Retorne SOMENTE o objeto JSON.
2. Sem markdown, sem blocos de código.
3. Sem texto antes ou depois.
4. Tema: %s.
5. Dificuldade: %s.
""".formatted(l, d);

        try {
            String response = ia.chat(prompt);
            String jsonContent = extractJson(response);
            
            // Valida e parseia o JSON
            JsonNode node = objectMapper.readTree(jsonContent);
            Model quiz = objectMapper.treeToValue(node, Model.class);
            
            return new Model2(
                quiz.getTitle(), 
                quiz.getDescription(), 
                quiz.getAnswer(), 
                
                d
            );
            
        } catch (Exception e) {
            // Se falhar, tenta mais uma vez com tema/dificuldade diferentes
            return crateNewQuizzFallback();
        }
    }
    
    private String extractJson(String response) {
        // Primeiro, tenta encontrar JSON com regex
        Matcher matcher = JSON_PATTERN.matcher(response);
        if (matcher.find()) {
            String json = matcher.group(0);
            
            // Limpeza básica
            json = json.trim()
                      .replaceAll("^```(?:json)?\\s*", "")
                      .replaceAll("\\s*```$", "");
            
            // Valida se tem estrutura mínima de JSON
            if (json.startsWith("{") && json.endsWith("}")) {
                return json;
            }
        }
        
        // Fallback: remove blocos de markdown manualmente
        String cleaned = response.trim();
        if (cleaned.startsWith("```")) {
            int start = cleaned.indexOf("\n");
            int end = cleaned.lastIndexOf("```");
            if (start > 0 && end > start) {
                cleaned = cleaned.substring(start, end).trim();
            } else {
                cleaned = cleaned.replaceAll("```(?:json)?", "").trim();
            }
        }
        
        return cleaned;
    }
    
    private Model2 crateNewQuizzFallback() {
        // Fallback mais conservador
        String l = createL();
        String d = createD();
        
        // Prompt mais restritivo para o fallback
        String fallbackPrompt = """
Retorne APENAS JSON no formato:
{"title":"","description":"","answer":""}
Tema: %s. Dificuldade: %s.
Exemplo: {"title":"Herança Java","description":"O que é herança em Java?","answer":"Mecanismo de reuso onde uma classe adquire propriedades de outra"}
""".formatted(l, d);
        
        try {
            String response = ia.chat(fallbackPrompt);
            String jsonContent = response.trim()
                .replaceAll("^```(?:json)?", "")
                .replaceAll("```$", "")
                .trim();
            
            JsonNode node = objectMapper.readTree(jsonContent);
            Model quiz = objectMapper.treeToValue(node, Model.class);
            
            return new Model2(
                quiz.getTitle(), 
                quiz.getDescription(), 
                quiz.getAnswer(), 
                
                d
            );
            
        } catch (Exception e) {
            // Último fallback: quiz manual
            return createManualQuiz(l, d);
        }
    }
    
    private Model2 createManualQuiz(String language, String difficulty) {
        // Quizzes manuais de fallback
        String title = "Quiz sobre " + language;
        String description = "Pergunta sobre conceitos básicos de " + language;
        String answer = "Resposta padrão para " + language;
        
        return new Model2(title, description, answer, difficulty);
    }

    private String createL() {
        return lin[random.nextInt(lin.length)];
    }

    private String createD() {
        return dif[random.nextInt(dif.length)];
    }
}