package com.krakpaip;

import net.dankito.readability4j.Article;

public record WebpageContentResult(
        String url,
        String title,
        String content,
        RetrievalStatus status,
        String error,
        int httpStatus
) {
    public static WebpageContentResult unavailable(int status, String error, String url) {
        return new WebpageContentResult(url, null, null, RetrievalStatus.UNAVAILABLE, error, status);
    }

    public static WebpageContentResult failed(int status, String error, String url) {
        return new WebpageContentResult(url, null, null, RetrievalStatus.FAILED, error, status);
    }

    public static WebpageContentResult success(int status, String url, Article article) {
        return new WebpageContentResult(
                url,
                article.getTitle(),
                article.getTextContent(),
                RetrievalStatus.SUCCESS,
                null,
                status
        );
    }

    public boolean isSuccess() {
        return this.status == RetrievalStatus.SUCCESS;
    }

    public enum RetrievalStatus {
        SUCCESS,
        FAILED,
        UNAVAILABLE
    }
}

