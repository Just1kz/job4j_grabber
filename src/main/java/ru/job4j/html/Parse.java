package ru.job4j.html;

import ru.job4j.model.Post;

import java.io.IOException;
import java.util.List;

public interface Parse {
    public List<Post> list(String link) throws Exception;

    public Post detail(String link) throws Exception;
}
