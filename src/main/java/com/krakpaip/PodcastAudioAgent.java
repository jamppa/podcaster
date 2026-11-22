package com.krakpaip;

import com.google.genai.Client;
import com.google.genai.types.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PodcastAudioAgent {

    private final Client client;
    private final GenerateContentConfig config;

    PodcastAudioAgent(AppConfiguration appConfiguration) {
        this.client = new Client.Builder()
                .apiKey(appConfiguration.geminiApiKey())
                .httpOptions(HttpOptions.builder().apiVersion("v1beta").build())
                .build();

        PrebuiltVoiceConfig prebuiltVoice = PrebuiltVoiceConfig.builder()
                .voiceName("Charon")
                .build();
        VoiceConfig voiceConfig = VoiceConfig.builder()
                .prebuiltVoiceConfig(prebuiltVoice)
                .build();
        SpeechConfig speechConfig = SpeechConfig.builder()
                .voiceConfig(voiceConfig)
                .build();

        this.config = GenerateContentConfig.builder()
                .responseModalities(List.of("AUDIO"))
                .speechConfig(speechConfig)
                .build();
    }

    void createAudio(String script) {
        var response = client.models.generateContent("gemini-3.8-flash-tts", promptWithScript(script), config);

        if (response.candidates().isPresent() && !response.candidates().get().isEmpty()) {
            var candidate = response.candidates().get().getFirst();

            var rawAudio = candidate.content()
                    .flatMap(Content::parts)
                    .filter(parts -> !parts.isEmpty())
                    .map(List::getFirst)
                    .flatMap(Part::inlineData)
                    .flatMap(Blob::data);

            if (rawAudio.isPresent()) {
                try {
                    String outputFile = filename();
                    save(rawAudio.get(), outputFile);
                    System.out.println("Audio saved successfully to " + outputFile);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save audio file", e);
                }
            } else {
                throw new RuntimeException("Candidate found but inline audio data was missing.");
            }
        } else {
            String feedback = response.promptFeedback().map(Object::toString).orElse("No feedback available");
            System.err.println("Couldn't get audio content from Gemini API: " + feedback);
            throw new RuntimeException("Couldn't get audio content from Gemini API: " + feedback);
        }
    }

    private String filename() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        return "podcaster_episode_" + timestamp + ".wav";
    }

    private void save(byte[] rawAudio, String file) throws IOException {
        Files.write(Path.of(file), rawAudio);
    }

    private String promptWithScript(String script) {
        return """
                Read the text inside the <script> tags in a calm, authoritative, and engaging news anchor voice.
                Maintain a steady broadcast pace with natural pauses between sentences. Do not read the tags themselves:
                
                <script>
                    %s
                </script>
                """.formatted(script);
    }
}
