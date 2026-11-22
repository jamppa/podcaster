package com.krakpaip;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public class PodcastScriptWriterAgent {

    private final PodcastScriptWriter scriptWriterAgent;

    public PodcastScriptWriterAgent(AppConfiguration appConfiguration) {
        ChatModel chatModel = GoogleGenAiChatModel.builder()
                .apiKey(appConfiguration.geminiApiKey())
                .modelName(appConfiguration.model())
                .temperature(0.5)
                .logRequestsAndResponses(true)
                .build();
        this.scriptWriterAgent = AiServices
                .builder(PodcastScriptWriter.class)
                .chatModel(chatModel)
                .build();
    }

    @SystemMessage(
            """
                You are a broadcast news copywriter and solo audio script producer. Your task is to transform raw researched articles, bulleted summaries, and notes into an engaging, authoritative, and natural-sounding solo news briefing script.
            
                ### OBJECTIVE & FORMAT
                - Produce a continuous, single-narrator script ready for direct ingestion by a Text-to-Speech (TTS) model.
                - Tone: Informative, concise, and professional yet accessible.
            
                ### SCRIPTING FOR TTS & AUDIO DELIVERY
                - Write for the ear: Short punchy sentences, active voice, expand symbols and numbers (e.g., "$1.5B" -> "one point five billion dollars", "%" -> "percent").
                - Guide cadence using natural punctuation (em-dashes "—", ellipses "...", commas).
                - Strictly NO markdown tags (no bold, bullets, headers), NO stage directions (no `[pause]`), and NO speaker labels.

                ### STRUCTURAL FLOW
                1. Cold Lead: Hook the listener immediately with the most impactful headline.
                2. Top Stories & Segments: Group logically with natural spoken transitions.
                3. Sign-off: Brief synthesis and professional sign-off.

                ### INPUT FORMAT
                - `<briefing>`: High-level episode summary or target angle.
                - `<source_material>`: Researched text and scraped content.

                ### OUTPUT CONSTRAINTS
                Return ONLY the clean spoken text. No intro/outro commentary, no labels.
            """)
    interface PodcastScriptWriter {
        @UserMessage(
        """
            Please generate the podcast script based on the following context.

            <briefing>
            {{briefing}}
            </briefing>

            <source_material>
            {{sourceMaterial}}
            </source_material>
        """)
        String generateScript(
                @V("briefing") String briefing,
                @V("sourceMaterial") String sourceMaterial
        );
    }

    String writeScript(PodcastResearchAgent.ResearchResult researchResult) {
        return scriptWriterAgent.generateScript(researchResult.summary(), researchResult.content());
    }
}
