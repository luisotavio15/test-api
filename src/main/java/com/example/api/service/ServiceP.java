package com.example.api.service;

import java.util.Random;

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

    private final String[] lin = { "Java", "cobol", "javascript", "pawno", "c++", "c", "py" };
    private final String[] dif = { "estagiario", "junior", "pleno", "senior", "Engenharia de Software",
            "arquiteto de software" };

    public ServiceP() {
        random = new Random();
        ia = OpenAiChatModel.builder()
                .apiKey(System.getenv("API_KEY")) 
                .baseUrl("https://api.groq.com/openai/v1") 
                .modelName("llama-3.1-8b-instant")               
                .build();
    }

    public Model2 crateNewQuizz() {
        String l = createL();
        String d = createD();
        
        String promat = """
Você é uma inteligência artificial especializada em criar quizzes de programação.
Gere exatamente UM quiz em formato JSON válido, sem blocos de markdown, sem texto extra, apenas o objeto puro.

Formato obrigatório:
{
  "title": "Título curto e claro",
  "description": "Pergunta objetiva (máx. 2 linhas)",
  "answer": "Resposta direta (máx. 1 linha)"
}

Regras:
1. Retorne somente UM objeto JSON.
2. Não inclua comentários, explicações ou texto fora do JSON.
3. O tema do quiz é: %s.
4. O nível de dificuldade é: %s.
5. Seja conciso e direto.
""".formatted(l, d);

        String res = ia.chat(promat);
        res = res.replaceAll("```json", "") .replaceAll("```", "") .trim(); 
        res = res.replaceAll(":\\s*\"([^\"]*?)\"([^\"]*?)\"", ": \"$1\\\"$2\\\"\"");
        res = res.replaceAll(":(\\s*)\"([^\"]*?)\"", ": \"$2\""); 
        res = res.replaceAll("\\\"","\\\\\"");

        if (!res.endsWith("}")) { 
            res = res + "}"; 
        }

        ObjectMapper op = new ObjectMapper();
        Model quiz = new Model();
        try {
            JsonNode node = op.readTree(res);
            quiz = op.treeToValue(node, Model.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON da IA: " + res, e);
        }
        Model2 quiz2 = new Model2(quiz.getTitle(),  quiz.getDescription(), quiz.getAnswer(), d);
        
        return quiz2;
    }

    private String createL() {
        return lin[random.nextInt(lin.length)];
    }

    private String createD() {
        return dif[random.nextInt(dif.length)];
    }
}
