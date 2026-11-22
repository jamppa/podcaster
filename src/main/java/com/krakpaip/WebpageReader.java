package com.krakpaip;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class WebpageReader {

    static WebpageContentResult read(String url)  {
        try {
            var response = Jsoup.connect(url)
                    .userAgent("PodcastResearchAgent/1.0")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .timeout(15_000)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .execute();

            if (response.statusCode() != 200) {
                return WebpageContentResult.unavailable(
                        response.statusCode(),
                        response.statusMessage(),
                        url
                );
            }

            if (response.contentType() == null || !Objects.requireNonNull(response.contentType()).contains("text/html")) {
                return WebpageContentResult.failed(
                        response.statusCode(),
                        "Unsupported content-type: " + response.contentType(),
                        url
                );
            }

            Document doc = response.parse();
            Readability4J readability4J = new Readability4J(url, doc);
            Article article = readability4J.parse();

            return WebpageContentResult.success(response.statusCode(), url, article);
        } catch (IOException e) {
            return WebpageContentResult.failed(-1, e.getMessage(), url);
        }
    }
}
