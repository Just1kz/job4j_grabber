package ru.job4j.model;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private String name;
    private URL url;
    private LocalDateTime dateTime;

    public Post(String name, URL url, LocalDateTime dateTime) {
        this.name = name;
        this.url = url;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
                && Objects.equals(url, post.url)
                && Objects.equals(dateTime, post.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, dateTime);
    }

    @Override
    public String toString() {
        return "Post{"
                + "name='"
                + name
                + '\''
                +  ", url="
                + url
                + ", LDT="
                + dateTime
                + '}';
    }
}
