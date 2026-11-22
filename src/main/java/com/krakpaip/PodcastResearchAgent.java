package com.krakpaip;

import com.krakpaip.GoogleSearch.GoogleSearchResult;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

import java.util.ArrayList;
import java.util.List;

public class PodcastResearchAgent {

    private final ChatModel workerModel;

    public PodcastResearchAgent(AppConfiguration appConfiguration) {
        this.workerModel = GoogleGenAiChatModel.builder()
                .apiKey(appConfiguration.geminiApiKey())
                .modelName(appConfiguration.model())
                .build();
    }

    public record ResearchResult(String summary, String content) {
        static ResearchResult of(String summary, List<String> contents) {
            return new ResearchResult(summary, String.join("\n\n", contents));
        }
    }

    ResearchResult research(
            String podcastSummary,
            GoogleSearchResult googleSearchResult
    ) {

        List<String> contents = new ArrayList<>();

        for (String url : googleSearchResult.links()) {
            WebpageContentResult result = WebpageReader.read(url);
            if (result.isSuccess()) {
                contents.add(extractRelevant(podcastSummary, result));
            }
            System.out.println(result);
        }

        return ResearchResult.of(podcastSummary, contents);
    }

    private String extractRelevant(String podcastSummary, WebpageContentResult result) {
        String prompt = """
                 You are doing research for a podcast, whose details are:
                 <details>{podcast_description}</details>
                
                 Extract whatever relevant information you can from this info
                 you've found on the web:
                
                 <webpage_title>{webpage_title}</webpage_title>
                 <webpage_content>{webpage_content}</webpage_content>.
                
                 Just include the extracted text and nothing else in your response.
                """;
        return workerModel.chat(prompt
                .replace("{podcast_description}", podcastSummary)
                .replace("{webpage_title}", result.title())
                .replace("{webpage_content}", result.content()));
    }
}
