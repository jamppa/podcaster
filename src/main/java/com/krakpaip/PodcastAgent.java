package com.krakpaip;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PodcastAgent {

    public final Assistant podcastAssistant;

    public PodcastAgent(AppConfiguration appConfiguration) {
        ChatModel chatModel = GoogleGenAiChatModel
                .builder()
                .apiKey(appConfiguration.geminiApiKey())
                .modelName(appConfiguration.model())
                .temperature(0.5)
                .build();
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        PodcastTool podcastTool = new PodcastTool(
                new PodcastWebQueryAgent(appConfiguration),
                new GoogleSearch(appConfiguration),
                new PodcastResearchAgent(appConfiguration),
                new PodcastScriptWriterAgent(appConfiguration)
        );

        this.podcastAssistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .tools(podcastTool)
                .systemMessageProvider((_) -> """
            You are a podcast producer, creating news-based and explainer podcasts for people on any topic they choose.
            Today's date is %s
            
            Here is the plan to follow step by step to create the podcast:
            <plan>
             1. INFORMATION GATHERING:
             When the user describes the podcast they want, do not proceed to the
             next step until you've obtained the following information:
              * The topic of the podcast.
              * How long the podcast should be. (For example, five minutes long.)
             However, do not ask the user about the podcast style. Assume that the
             podcast style is a single host reporting news and insight.
            
             2. SUMMARY CREATION:
             Next, create a simple summary describing the type of podcast the user wants.
             Ensure that this summary includes the desired time length of the podcast.
             For example, the summary might be: "A 3-minute podcast on the latest news, insights, and updates in the field of quantum physics"
            
            3. TOOL EXECUTION:
            Immediately after creating the summary, you must call the `initiate_podcast` tool. Pass the exact summary you created in step 2 as the argument to the tool.
            </plan>
            """.formatted(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
                .build();

    }

    public interface Assistant {
        String chat(String message);
    }

}
