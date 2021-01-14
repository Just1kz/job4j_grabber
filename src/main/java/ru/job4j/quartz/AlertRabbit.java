package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit implements AutoCloseable {
    private final int interval;
    private final String url;
    private final String login;
    private final String password;

    private Connection connection;
    private final Map<String, String> values = new HashMap<String, String>();

    public AlertRabbit() {
        getSettingsFileProperties();
        this.interval = Integer.parseInt(values.get("rabbit.interval"));
        this.url = values.get("url");
        this.login = values.get("login");
        this.password = values.get("password");
        if (url == null
                || login == null
                || password == null
                || interval == 0) {
            throw new IllegalArgumentException("You set not all parameters in file rabbit.properties."
                    + " Check your file.");
        }
    }

    private Connection initConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection(url, login, password);
        return this.connection;
    }

    public static void main(String[] args) throws Exception {
        AlertRabbit alertRabbit = new AlertRabbit();
        try (Connection connection = alertRabbit.initConnection()) {
            alertRabbit.createTable(connection);
            try {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("connection", connection);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(alertRabbit.interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
            } catch (Exception se) {
                se.printStackTrace();
            }
        }
    }

    public void getSettingsFileProperties() {
        try (BufferedReader read = new BufferedReader(new FileReader(".\\src\\main\\resources\\rabbit.properties"))) {
            read.lines()
                    .filter(x -> x.length() != 0 && !x.startsWith("#"))
                    .map(line -> line.split("="))
                    .forEach(x -> values.put(x[0], x[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTable(Connection connection) throws Exception {
            try (Statement statement = connection.createStatement()) {
                String sql = String.format(
                        "create table if not exists %s(%s, %s);",
                        "rabbit",
                        "created_date date",
                        "created_time time"
                );
                statement.execute(sql);
            }
        }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() throws Exception {
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            addJob("date '" + formatDate.format(date) + "'",
                    "time '" + formatTime.format(date) + "'");
        }

        public void addJob(String date, String time)
                throws Exception {
            try (Connection connection = new AlertRabbit().initConnection()) {
                try (Statement statement = connection.createStatement()) {
                    String sql = String.format(
                            "insert into rabbit(created_date, created_time) values (%s, %s);",
                            date,
                            time
                    );
                    statement.execute(sql);
                }
            }
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
                System.out.println("Rabbit runs here ...");
                System.out.println("Write time in BD true");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
