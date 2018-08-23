package com.example.raghav.newsify;

public class News {
    private String articleTitle, articleDate, articleUrl;

    public News(String articleTitle, String articleDate, String articleUrl) {
        this.articleTitle = articleTitle;
        this.articleDate = articleDate;
        this.articleUrl = articleUrl;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleDate() {
        return articleDate;
    }

    public String getArticleUrl() {
        return articleUrl;
    }
}
