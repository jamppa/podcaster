package com.krakpaip;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

public class PodcastWebQueryAgent {

    private final ChatModel workerModel;

    public PodcastWebQueryAgent(AppConfiguration appConfiguration) {
        this.workerModel = GoogleGenAiChatModel.builder()
                .apiKey(appConfiguration.geminiApiKey())
                .modelName(appConfiguration.model())
                .build();
    }

    String googleSearchQuery(String podcastSummary) {
        String prompt = """
                You are doing research for a podcat, whose details are as follows:
                <details>
                 %s
                </details>
                Generate a Google web search query to help do research for the podcast.
                
                <instructions>
                 The web search query should be specific and succinct, no more than 6
                 words. For example, if the podcast topic is about the latest news
                 in Finland, your query would be simply: Finland latest news
                </instructions>
                """.formatted(podcastSummary);

        return workerModel.chat(prompt);
    }
}
