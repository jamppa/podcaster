package com.krakpaip;

import dev.langchain4j.agent.tool.Tool;

public class PodcastTool {

    private final PodcastWebQueryAgent podcastWebQueryAgent;
    private final GoogleSearch googleSearch;
    private final PodcastResearchAgent podcastResearchAgent;
    private final PodcastScriptWriterAgent podcastScriptWriterAgent;

    public PodcastTool(
            PodcastWebQueryAgent podcastWebQueryAgent,
            GoogleSearch googleSearch,
            PodcastResearchAgent podcastResearchAgent, PodcastScriptWriterAgent podcastScriptWriterAgent
    ) {
        this.podcastWebQueryAgent = podcastWebQueryAgent;
        this.googleSearch = googleSearch;
        this.podcastResearchAgent = podcastResearchAgent;
        this.podcastScriptWriterAgent = podcastScriptWriterAgent;
    }

    @Tool(
            name = "initiate_podcast" ,
            value = "Generates an audio podcast as a file based on the given summary."
    )
    boolean initiatePodcast(String summary) {
        System.out.println("PODCAST SUMMARY: " + summary);

        var webQuery = podcastWebQueryAgent.googleSearchQuery(summary);
        System.out.println("PODCAST WEB QUERY: " + webQuery);

        var searchResult = googleSearch.search(webQuery);
        System.out.println("PODCAST SEARCH RESULT: " + searchResult);

        var researchResult = podcastResearchAgent.research(summary, searchResult);
        System.out.println("RESEARCHED AND SYNTHESIZED CONTENT: " + researchResult);

        var podcastScript = podcastScriptWriterAgent.writeScript(researchResult);
        System.out.println("PODCAST SCRIPT: " + podcastScript);



        return true;
    }
}
