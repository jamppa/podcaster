package com.krakpaip;

import io.github.cdimascio.dotenv.Dotenv;

public record AppConfiguration(
        String geminiApiKey,
        String model,
        String searchApi,
        String searchApiKey
) {

    static AppConfiguration load() {
        Dotenv env = Dotenv.configure().ignoreIfMissing().load();
        return new AppConfiguration(
                env.get("GEMINI_API_KEY"),
                env.get("GEMINI_MODEL"),
                env.get("SEARCH_API"),
                env.get("SEARCH_API_KEY")
        );
    }
}
