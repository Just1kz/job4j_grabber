package ru.job4j.model;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private String name;
    private String body;
    private String link;
    private LocalDateTime dateTime;

//    public Post() {
//    }

    public Post(String name, String body, String url, LocalDateTime dateTime) {
        this.name = name;
        this.body = body;
        this.link = url;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public String getLink() {
        return link;
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
                && Objects.equals(link, post.link)
                && Objects.equals(dateTime, post.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, body, link, dateTime);
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
                + link
                + ", dateTime="
                + dateTime
                + '}';
    }
}
