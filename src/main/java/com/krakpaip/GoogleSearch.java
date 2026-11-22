package com.krakpaip;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GoogleSearch {

    private final AppConfiguration appConfiguration;

    public GoogleSearch(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public record GoogleSearchResult(List<String> links) {
        public static GoogleSearchResult ofEmpty() {
            return new GoogleSearchResult(List.of());
        }

        public static GoogleSearchResult fromJson(JsonObject json) {
            var results = json.getAsJsonArray("news_results");
            if (results == null) {
                return ofEmpty();
            }

            List<String> links = new ArrayList<>();

            for (int i = 0; i < Math.min(results.size(), 10); i++) {
                var item = results.get(i).getAsJsonObject();
                if (item.has("link") && !item.get("link").isJsonNull()) {
                    var l = item.get("link").getAsString();
                    if (l.startsWith("http://") || l.startsWith("https://")) {
                        links.add(l);
                    }
                }
            }

            return new GoogleSearchResult(links);
        }
    }

    GoogleSearchResult search(String query)  {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder().uri(getUri(query)).GET().build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var responseAsJson = JsonParser.parseString(response.body()).getAsJsonObject();

            return GoogleSearchResult.fromJson(responseAsJson);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return GoogleSearchResult.ofEmpty();
    }

    private URI getUri(String query) {
        var q = URLEncoder.encode(
                query,
                StandardCharsets.UTF_8
        );
        return URI.create(appConfiguration.searchApi() + "&q=" + q + "&api_key=" + appConfiguration.searchApiKey());
    }
}
