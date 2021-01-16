package ru.job4j.db;

import ru.job4j.html.SqlRuParse;
import ru.job4j.model.Post;
import ru.job4j.quartz.AlertRabbit;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SQLStore implements Store, AutoCloseable {
    private Connection connection;
    private Properties properties;

    public void initProperties() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(
                "rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            this.properties = config;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void initConnection() {
        try {
            Class.forName(this.properties.getProperty("driver-class-name"));
            this.connection = DriverManager.getConnection(
                    this.properties.getProperty("url"),
                    this.properties.getProperty("login"),
                    this.properties.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void createTable() throws SQLException {
            try (Statement statement = connection.createStatement()) {
                String sql =
                        "drop table if exists grabber.public.post;"
                                + "create table if not exists grabber.public.post("
                                + "id serial primary key,"
                                + "name text unique not null,"
                                + "body text,"
                                + "link text,"
                                + "created_dateWithTime text"
                                + ");";
                statement.execute(sql);
            }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = this.connection.prepareStatement(
                "insert into post(name, body, link, created_dateWithTime) "
                        + "values(?, ?, ?, ?);")) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getBody());
            ps.setString(3, post.getLink());
            ps.setString(4, post.getDateTime().toString());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select * from post;")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rsl.add(new Post(
                            rs.getString("name"),
                            rs.getString("body"),
                            rs.getString("link"),
                            createLocalDateTime(rs.getString("created_dateWithTime"))
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(String id) {
        Post rsl = null;
        try (PreparedStatement ps = connection.prepareStatement(
                "select * from post where id = ?;")) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rsl = new Post(
                            rs.getString("name"),
                            rs.getString("body"),
                            rs.getString("link"),
                            createLocalDateTime(rs.getString("created_dateWithTime"))
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public List<Post> getNameJava() {
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "select * from post "
                        + "where name LIKE '%Java%'"
                        + "or name LIKE '%java%'"
                        + "or name LIKE 'Java%'"
                        + "or name LIKE 'java%';")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rsl.add(new Post(
                            rs.getString("name"),
                            rs.getString("body"),
                            rs.getString("link"),
                            createLocalDateTime(rs.getString("created_dateWithTime"))
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public void close() throws Exception {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    public LocalTime createLocalTime(String text) {
        String[] x = text.split(":");
        int hours = Integer.parseInt(x[0]);
        int minutes = Integer.parseInt(x[1]);
        return LocalTime.of(hours, minutes);
    }

    public LocalDate createLocalDate(String text) {
        String[] x = text.split("-");
        int year = Integer.parseInt(x[0]);
        int month = Integer.parseInt(x[1]);
        int day = Integer.parseInt(x[2]);
        return LocalDate.of(year, month, day);
    }

    public LocalDateTime createLocalDateTime(String text) {
        String[] x = text.split("T");
        return LocalDateTime.of(
                createLocalDate(x[0]),
                createLocalTime(x[1])
                );
    }

    public static void main(String[] args) throws Exception {
        SQLStore sqlStore = new SQLStore();
        sqlStore.initProperties();
        sqlStore.initConnection();
        sqlStore.createTable();


        List<Post> rsl = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String url = "https://www.sql.ru/forum/job-offers/";
            SqlRuParse rslX = new SqlRuParse();
            rsl.addAll(rslX.list(url + i));
        }

        for (Post x : rsl) {
          sqlStore.save(x);
        }

        System.out.println("Вывод всех объявлений из БД");
        System.out.println(sqlStore.getAll().toString());
        System.out.println(" ");
        System.out.println("Вывод объявления с ID 10");
        System.out.println(sqlStore.findById("10"));
        System.out.println("Вывод объявлений по Java");
        System.out.println(sqlStore.getNameJava().toString());
    }
}
