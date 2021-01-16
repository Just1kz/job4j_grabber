package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit implements AutoCloseable {
    private  int interval;

    private Connection connection;

    public AlertRabbit() {
        initConnection();
    }

    public AlertRabbit(Connection connection) {
        this.connection = connection;
    }

    public void initConnection() {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(
                "rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("login"),
                    config.getProperty("password")
            );
            this.interval =  Integer.parseInt(config.getProperty("rabbit.interval"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        AlertRabbit alertRabbit = new AlertRabbit();
        try (Connection connection = alertRabbit.connection) {
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

    public void createTable(Connection connection) throws Exception {
            try (Statement statement = connection.createStatement()) {
                String sql = String.format(
                        "create table if not exists %s(%s, %s);",
                        "rabbit",
                        "created_date text",
                        "created_time text"
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
//            addJob("date '" + formatDate.format(date) + "'",
//                    "time '" + formatTime.format(date) + "'");
            addJob(formatDate.format(date), formatTime.format(date));
        }

        public void addJob(String date, String time)
                throws Exception {
            AlertRabbit alertRabbit = new AlertRabbit();
//            try (Connection connection = alertRabbit.connection) {
//                try (Statement statement = connection.createStatement()) {
//                    String sql = String.format(
//                            "insert into rabbit(created_date, created_time) values (%s, %s);",
//                            date,
//                            time
//                    );
//                    statement.execute(sql);
//                }
//            }
            try (PreparedStatement ps = alertRabbit.connection.prepareStatement(
                    "insert into rabbit(created_date, created_time) values(?, ?)")) {
                ps.setString(1, date);
                ps.setString(2, time);
                ps.execute();
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
