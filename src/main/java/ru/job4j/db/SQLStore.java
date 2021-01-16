package ru.job4j.db;

import ru.job4j.model.Post;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

public class SQLStore implements Store , AutoCloseable {

    private Connection cnn;

//    public PsqlStore(Properties cfg) {
//        try {
//            Class.forName(cfg.getProperty("jdbc.driver"));
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//        /* cnn = DriverManager.getConnection(...); */
//    }

    @Override
    public void save(Post post) {

    }

    @Override
    public List<Post> getAll() {
        return null;
    }

    @Override
    public Post findById(String id) {
        return null;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
