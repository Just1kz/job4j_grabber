package ru.job4j.model;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private String name;
    private String body;
    private URL url;
    private LocalDateTime dateTime;

    public Post() {
    }

    public Post(String name, String body, URL url, LocalDateTime dateTime) {
        this.name = name;
        this.body = body;
        this.url = url;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public URL getUrl() {
        return url;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(name, post.name)
                && Objects.equals(body, post.body)
                && Objects.equals(url, post.url)
                && Objects.equals(dateTime, post.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, body, url, dateTime);
    }

    @Override
    public String toString() {
        return "Post{"
                + "name='"
                + name
                + '\''
                + ", body='"
                + body
                + '\''
                + ", url="
                + url
                + ", dateTime="
                + dateTime
                + '}';
    }
}
